package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.config.properties.OtpSecurityProperties;
import com.teamsolution.auth.dto.internal.RefreshTokenData;
import com.teamsolution.auth.dto.request.ChangePasswordRequest;
import com.teamsolution.auth.dto.request.GoogleLoginRequest;
import com.teamsolution.auth.dto.request.LoginRequest;
import com.teamsolution.auth.dto.request.LogoutRequest;
import com.teamsolution.auth.dto.request.RefreshRequest;
import com.teamsolution.auth.dto.request.RegisterRequest;
import com.teamsolution.auth.dto.request.ResendOtpChangeEmailRequest;
import com.teamsolution.auth.dto.request.ResendOtpPasswordResetRequest;
import com.teamsolution.auth.dto.request.ResendVerificationOtpRequest;
import com.teamsolution.auth.dto.request.ResetPasswordRequest;
import com.teamsolution.auth.dto.request.SendOtpChangeEmailRequest;
import com.teamsolution.auth.dto.request.SendOtpResetPasswordRequest;
import com.teamsolution.auth.dto.request.SwitchRoleRequest;
import com.teamsolution.auth.dto.request.VerifyChangeEmailRequest;
import com.teamsolution.auth.dto.request.VerifyEmailRequest;
import com.teamsolution.auth.dto.request.VerifyPasswordResetRequest;
import com.teamsolution.auth.dto.response.AuthResponse;
import com.teamsolution.auth.dto.response.LoginResponse;
import com.teamsolution.auth.dto.response.PendingAccountResponse;
import com.teamsolution.auth.dto.response.ProfileResponse;
import com.teamsolution.auth.dto.response.RegisterResponse;
import com.teamsolution.auth.dto.response.ResendOtpChangeEmailResponse;
import com.teamsolution.auth.dto.response.ResendOtpPasswordResetResponse;
import com.teamsolution.auth.dto.response.ResendVerificationOtpResponse;
import com.teamsolution.auth.dto.response.SendOtpChangeEmailResponse;
import com.teamsolution.auth.dto.response.SendOtpResetPasswordResponse;
import com.teamsolution.auth.dto.response.VerifyPasswordResetResponse;
import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.auth.enums.AccountLockReason;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.grpc.client.CustomerGrpcClient;
import com.teamsolution.auth.security.CustomUserDetails;
import com.teamsolution.auth.service.AccountRoleService;
import com.teamsolution.auth.service.AccountSecurityService;
import com.teamsolution.auth.service.AccountService;
import com.teamsolution.auth.service.AuthService;
import com.teamsolution.auth.service.JwtTokenService;
import com.teamsolution.auth.service.RefreshTokenService;
import com.teamsolution.auth.service.ResetTokenService;
import com.teamsolution.auth.service.VerificationTokenService;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import com.teamsolution.common.core.enums.auth.SystemRole;
import com.teamsolution.common.core.enums.auth.TokenType;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.kafka.constant.NotificationEventType;
import com.teamsolution.common.redis.constant.RedisKeys;
import com.teamsolution.common.redis.service.RedisService;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final OtpSecurityProperties otpSecurityProperties;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  private final AccountService accountService;
  private final AccountRoleService accountRoleService;
  private final RefreshTokenService refreshTokenService;
  private final JwtTokenService jwtTokenService;
  private final VerificationTokenService verificationTokenService;
  private final RedisService redisService;
  private final ResetTokenService resetTokenService;
  private final AccountSecurityService accountSecurityService;

  private final CustomerGrpcClient customerGrpcClient;

  @Override
  @Transactional
  public LoginResponse login(LoginRequest request) {
    CustomUserDetails userDetails = authenticate(request.email(), request.password());

    Account account = userDetails.account();

    return switch (account.getStatus()) {
      case AccountStatus.PENDING -> handlePendingLogin(account);
      case AccountStatus.ACTIVE -> {
        account.setLastLoginAt(LocalDateTime.now());
        accountService.save(account);
        yield issueTokensAndSave(account.getId(), null);
      }
      default -> throw new AppException(ErrorCode.ACCOUNT_STATUS_INVALID);
    };
  }

  @Override
  @Transactional
  public RegisterResponse register(RegisterRequest request) {
    Account account = accountService.findByEmailIncludingDeleted(request.email()).orElse(null);

    if (account == null) {
      String hashPassword = passwordEncoder.encode(request.password());
      Account newAccount =
          accountService.createNewAccount(
              request.email(),
              hashPassword,
              AccountStatus.PENDING,
              AccountRoleStatus.PENDING,
              request.fullName(),
              request.phone(),
              "");

      verificationTokenService.createOtpFlow(
          newAccount, NotificationEventType.EMAIL_VERIFICATION, TokenType.EMAIL_VERIFICATION);

      return new PendingAccountResponse(otpSecurityProperties.getExpiresInSeconds());
    }

    return switch (account.getStatus()) {
      case PENDING -> {
        handleOtpFlow(
            account, NotificationEventType.EMAIL_VERIFICATION, TokenType.EMAIL_VERIFICATION);
        yield new PendingAccountResponse(otpSecurityProperties.getExpiresInSeconds());
      }
      case ACTIVE -> {
        yield handleByCustomerRoleStatus(account, request);
      }
      case INACTIVE -> {
        accountSecurityService.reactivate(account);
        yield handleByCustomerRoleStatus(account, request);
      }
      case SUSPENDED -> {
        if (account.getLockReason() == null) {
          throw new AppException(ErrorCode.ACCOUNT_STATUS_INVALID);
        }
        switch (account.getLockReason()) {
          case ADMIN_LOCK -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_ADMIN);
          case FAILED_ATTEMPTS -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
          default -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_SYSTEM);
        }
      }
      case DELETED -> throw new AppException(ErrorCode.INVALID_CREDENTIALS);

      default -> throw new AppException(ErrorCode.ACCOUNT_STATUS_INVALID);
    };
  }

  @Override
  @Transactional
  public AuthResponse verifyEmail(VerifyEmailRequest request) {

    UUID accountId =
        verificationTokenService.verifyOtp(
            request.email(), request.otp(), TokenType.EMAIL_VERIFICATION);

    Account account = accountService.getByIdOrThrow(accountId);

    if (!account.getStatus().isVerifiable()) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_PENDING);
    }

    account.setStatus(AccountStatus.ACTIVE);
    accountService.save(account);

    accountRoleService.activateCustomerRole(accountId);

    return issueTokensAndSave(account.getId(), null);
  }

  @Override
  public ResendVerificationOtpResponse resendVerificationOtp(ResendVerificationOtpRequest request) {

    verificationTokenService.sendOtpFlow(
        request.email(), NotificationEventType.EMAIL_VERIFICATION, TokenType.EMAIL_VERIFICATION);

    return new ResendVerificationOtpResponse(otpSecurityProperties.getExpiresInSeconds());
  }

  @Override
  @Transactional
  public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
    Account account =
        accountService.findOrCreateGoogleAccount(
            request.email(), request.name(), request.picture());

    return switch (account.getStatus()) {
      case PENDING -> {
        account.setStatus(AccountStatus.ACTIVE);
        accountRoleService.activateCustomerRole(account.getId());
        accountService.save(account);
        yield issueTokensAndSave(account.getId(), null);
      }
      case ACTIVE -> {
        ensureCustomerRoleExists(account, request.name(), request.picture());
        yield issueTokensAndSave(account.getId(), null);
      }
      case INACTIVE -> {
        accountSecurityService.reactivate(account);
        ensureCustomerRoleExists(account, request.name(), request.picture());
        yield issueTokensAndSave(account.getId(), null);
      }
      case SUSPENDED -> {
        if (account.getLockReason() == null) {
          throw new AppException(ErrorCode.ACCOUNT_STATUS_INVALID);
        }
        switch (account.getLockReason()) {
          case ADMIN_LOCK -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_ADMIN);
          case FAILED_ATTEMPTS -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
          default -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_SYSTEM);
        }
      }
      case DELETED -> throw new AppException(ErrorCode.INVALID_CREDENTIALS);
      default -> throw new AppException(ErrorCode.ACCOUNT_STATUS_INVALID);
    };
  }

  @Override
  @Transactional
  public AuthResponse refresh(RefreshRequest request) {
    RefreshTokenData data = refreshTokenService.validate(request.refreshToken());
    return issueTokensAndSave(data.accountId(), data.selectedRole());
  }

  @Override
  @Transactional
  public AuthResponse switchRole(UUID accountId, SwitchRoleRequest request) {
    refreshTokenService.validate(request.refreshToken());
    return issueTokensAndSave(accountId, request.role());
  }

  @Override
  @Transactional(readOnly = true)
  public ProfileResponse getMe(UUID accountId) {
    Account account = accountService.getAccountWithActiveRolesByAccountId(accountId);

    Set<String> roles =
        account.getAccountRoles().stream()
            .filter(
                a ->
                    a.getRole().getStatus() == RoleStatus.ACTIVE
                        && !a.getRole().getIsDeleted()
                        && a.getStatus() == AccountRoleStatus.ACTIVE)
            .map(a -> a.getRole().getName())
            .collect(Collectors.toSet());

    return new ProfileResponse(account.getId(), account.getEmail(), account.getStatus(), roles);
  }

  @Override
  public SendOtpResetPasswordResponse sendOtpResetPassword(SendOtpResetPasswordRequest request) {
    Account account =
        accountService
            .findByEmailIncludingDeleted(request.email())
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

    handleOtpFlow(account, NotificationEventType.FORGOT_PASSWORD, TokenType.PASSWORD_RESET);

    return new SendOtpResetPasswordResponse(otpSecurityProperties.getExpiresInSeconds());
  }

  @Override
  public ResendOtpPasswordResetResponse resendOtpPasswordReset(
      ResendOtpPasswordResetRequest request) {
    verificationTokenService.sendOtpFlow(
        request.email(), NotificationEventType.FORGOT_PASSWORD, TokenType.PASSWORD_RESET);

    return new ResendOtpPasswordResetResponse(otpSecurityProperties.getExpiresInSeconds());
  }

  @Override
  public VerifyPasswordResetResponse verifyResetPassword(VerifyPasswordResetRequest request) {
    UUID accountId =
        verificationTokenService.verifyOtp(
            request.email(), request.otp(), TokenType.PASSWORD_RESET);

    String resetToken = resetTokenService.generate(accountId);

    return new VerifyPasswordResetResponse(resetToken);
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    UUID accountId = resetTokenService.peek(request.resetToken());

    Account account = accountService.getByIdOrThrow(accountId);

    if (passwordEncoder.matches(request.password(), account.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
    }

    resetTokenService.consume(request.resetToken());

    account.setPassword(passwordEncoder.encode(request.password()));

    if (account.getStatus() == AccountStatus.SUSPENDED
        && account.getLockReason() == AccountLockReason.FAILED_ATTEMPTS) {
      account.setStatus(AccountStatus.ACTIVE);
      account.setLockedUntil(null);
      account.setLockReason(AccountLockReason.NONE);
    }

    accountService.save(account);
    refreshTokenService.revokeAllRefreshTokensByAccountId(accountId);
  }

  @Override
  @Transactional
  public void changePassword(UUID accountId, ChangePasswordRequest request) {
    Account account = accountService.getByIdOrThrow(accountId);

    if (!passwordEncoder.matches(request.oldPassword(), account.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_INCORRECT);
    }

    if (passwordEncoder.matches(request.newPassword(), account.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
    }

    account.setPassword(passwordEncoder.encode(request.newPassword()));
    accountService.save(account);

    refreshTokenService.revokeAllRefreshTokensByAccountId(account.getId());
  }

  @Override
  public SendOtpChangeEmailResponse sendOtpChangeEmail(
      SendOtpChangeEmailRequest request, String currentEmail) {

    System.out.println("otp time: " + otpSecurityProperties.getExpiresInSeconds());
    System.out.println("session time: " + otpSecurityProperties.getSessionExpiresInSeconds());

    accountService
        .findByEmailIncludingDeleted(request.newEmail())
        .ifPresent(
            acc -> {
              throw new AppException(ErrorCode.ACCOUNT_DUPLICATE_EMAIL);
            });

    if (request.newEmail().equalsIgnoreCase(currentEmail)) {
      throw new AppException(ErrorCode.EMAIL_SAME_AS_OLD);
    }

    String key = RedisKeys.otpKey(request.newEmail(), TokenType.CHANGE_EMAIL.name());

    if (redisService.exists(key)) {
      verificationTokenService.sendOtpFlow(
          request.newEmail(), NotificationEventType.CHANGE_EMAIL, TokenType.CHANGE_EMAIL);
    } else {
      verificationTokenService.createOtpFlowForEmail(
          request.newEmail(), NotificationEventType.CHANGE_EMAIL, TokenType.CHANGE_EMAIL, 0);
    }

    return new SendOtpChangeEmailResponse(otpSecurityProperties.getExpiresInSeconds());
  }

  @Override
  public ResendOtpChangeEmailResponse resendOtpChangeEmail(ResendOtpChangeEmailRequest request) {
    verificationTokenService.sendOtpFlow(
        request.newEmail(), NotificationEventType.CHANGE_EMAIL, TokenType.CHANGE_EMAIL);

    return new ResendOtpChangeEmailResponse(otpSecurityProperties.getExpiresInSeconds());
  }

  @Override
  public AuthResponse verifyChangeEmail(VerifyChangeEmailRequest request, String currentEmail) {
    verificationTokenService.verifyOtp(request.newEmail(), request.otp(), TokenType.CHANGE_EMAIL);

    Account account =
        accountService
            .findByEmailIncludingDeleted(currentEmail)
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

    accountService
        .findByEmailIncludingDeleted(request.newEmail())
        .ifPresent(
            acc -> {
              throw new AppException(ErrorCode.ACCOUNT_DUPLICATE_EMAIL);
            });

    account.setEmail(request.newEmail());
    accountService.save(account);

    refreshTokenService.revokeAllRefreshTokensByAccountId(account.getId());
    return issueTokensAndSave(account.getId(), null);
  }

  @Override
  @Transactional
  public void logout(LogoutRequest request) {
    refreshTokenService.revoke(request.refreshToken());
  }

  @Transactional
  @Override
  public void logoutAll(UUID accountId) {
    refreshTokenService.revokeAllRefreshTokensByAccountId(accountId);
  }

  private PendingAccountResponse handlePendingLogin(Account account) {
    handleOtpFlow(account, NotificationEventType.EMAIL_VERIFICATION, TokenType.EMAIL_VERIFICATION);
    return new PendingAccountResponse(otpSecurityProperties.getExpiresInSeconds());
  }

  private AuthResponse issueTokensAndSave(UUID accountId, String selectedRole) {
    AuthResponse tokens = jwtTokenService.generateTokens(accountId, selectedRole);
    refreshTokenService.save(accountId, tokens.refreshToken(), selectedRole);
    return tokens;
  }

  private CustomUserDetails authenticate(String email, String password) {
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password));
    return (CustomUserDetails) auth.getPrincipal();
  }

  private void handleOtpFlow(Account account, String eventType, TokenType tokenType) {
    String key = RedisKeys.otpKey(account.getEmail(), tokenType.name());

    if (redisService.exists(key)) {
      verificationTokenService.sendOtpFlow(account.getEmail(), eventType, tokenType);
    } else {
      verificationTokenService.createOtpFlow(account, eventType, tokenType);
    }
  }

  private AuthResponse handleByCustomerRoleStatus(Account account, RegisterRequest request) {

    AccountRole customerRole =
        accountRoleService
            .getByAccountIdAndRoleName(account.getId(), SystemRole.CUSTOMER)
            .orElse(null);

    if (customerRole == null) {
      accountRoleService.createCustomerAccountRole(
          account, AccountRoleStatus.ACTIVE, SystemRole.CUSTOMER);
      customerGrpcClient.createNewCustomer(
          account.getId(), request.fullName(), request.phone(), null);
      return issueTokensAndSave(account.getId(), null);
    }

    return switch (customerRole.getStatus()) {
      case ACTIVE -> throw new AppException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
      case REVOKED -> throw new AppException(ErrorCode.ACCOUNT_ROLE_REVOKED);
      case INACTIVE, PENDING, DELETED -> {
        customerRole.setStatus(AccountRoleStatus.ACTIVE);
        accountRoleService.save(customerRole);
        yield issueTokensAndSave(account.getId(), null);
      }
    };
  }

  private void ensureCustomerRoleExists(Account account, String name, String picture) {
    if (accountRoleService
        .getByAccountIdAndRoleName(account.getId(), SystemRole.CUSTOMER)
        .isEmpty()) {
      accountRoleService.createCustomerAccountRole(
          account, AccountRoleStatus.ACTIVE, SystemRole.CUSTOMER);
      customerGrpcClient.createNewCustomer(account.getId(), name, null, picture);
    }
  }
}
