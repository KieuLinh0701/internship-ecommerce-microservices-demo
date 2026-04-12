package com.teamsolution.auth.security;

import com.teamsolution.auth.config.properties.LoginSecurityProperties;
import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.service.AccountSecurityService;
import com.teamsolution.auth.service.AccountService;
import com.teamsolution.common.core.exception.AppException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final AccountSecurityService accountSecurityService;
  private final AccountService accountService;
  private final LoginSecurityProperties loginSecurityProperties;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    CustomUserDetails userDetails;
    try {
      userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
    } catch (UsernameNotFoundException e) {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    Account account = userDetails.account();

    if (!userDetails.isAccountNonLocked()) {
      switch (account.getLockReason()) {
        case ADMIN_LOCK -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_ADMIN);
        case FAILED_ATTEMPTS -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
        case SYSTEM -> throw new AppException(ErrorCode.ACCOUNT_LOCKED_SYSTEM);
      }
    }

    LocalDateTime now = LocalDateTime.now();
    if (account.getLockedUntil() != null && account.getLockedUntil().isAfter(now)) {

      long secondsLeft = Duration.between(now, account.getLockedUntil()).toSeconds();

      String timeLeft =
          secondsLeft < 60 ? secondsLeft + " seconds" : (secondsLeft / 60) + " minutes";

      throw new AppException(
          ErrorCode.ACCOUNT_TEMPORARILY_LOCKED, loginSecurityProperties.getMaxAttempts(), timeLeft);
    }

    if (!userDetails.isEnabled()) {
      accountSecurityService.reactivate(account);
    }

    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      // 1. Ghi nhận failed attempt và lấy số lần hiện tại (đã commit nhờ REQUIRES_NEW)
      int attempts = accountSecurityService.handleFailedAttempt(account.getId());

      // 2. Lấy lại thông tin account sau khi đã update (để có lockedUntil mới nhất)
      Account updatedAccount = accountService.getByIdOrThrow(account.getId());

      // 3. Xử lý ném lỗi cụ thể dựa trên số lần vi phạm
      if (attempts >= loginSecurityProperties.getMaxAttemptsPermanent()) {
        throw new AppException(ErrorCode.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
      }

      if (attempts >= loginSecurityProperties.getMaxAttempts()) {
        now = LocalDateTime.now();
        // Tính toán thời gian còn lại để trả về cho Frontend hiển thị
        long secondsLeft = Duration.between(now, updatedAccount.getLockedUntil()).toSeconds();

        String timeLeft =
            secondsLeft < 60 ? secondsLeft + " seconds" : (secondsLeft / 60) + " minutes";

        throw new AppException(
            ErrorCode.ACCOUNT_TEMPORARILY_LOCKED,
            loginSecurityProperties.getMaxAttempts(), // Số lần tối đa được phép
            timeLeft // Thời gian còn lại
            );
      }

      // Lỗi mặc định nếu chưa chạm ngưỡng khóa
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    accountSecurityService.resetFailedAttemp(account.getId());

    return new UsernamePasswordAuthenticationToken(
        userDetails, password, userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
