package com.teamsolution.auth.controller;

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
import com.teamsolution.auth.dto.response.ProfileResponse;
import com.teamsolution.auth.dto.response.RegisterResponse;
import com.teamsolution.auth.dto.response.ResendOtpChangeEmailResponse;
import com.teamsolution.auth.dto.response.ResendOtpPasswordResetResponse;
import com.teamsolution.auth.dto.response.ResendVerificationOtpResponse;
import com.teamsolution.auth.dto.response.SendOtpChangeEmailResponse;
import com.teamsolution.auth.dto.response.SendOtpResetPasswordResponse;
import com.teamsolution.auth.dto.response.VerifyPasswordResetResponse;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.service.AuthService;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {

    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/oauth2/google")
  public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(
      @Valid @RequestBody GoogleLoginRequest request) {

    AuthResponse response = authService.loginWithGoogle(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponse>> register(
      @Valid @RequestBody RegisterRequest request) {

    RegisterResponse response = authService.register(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/verify-email")
  public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest request) {

    AuthResponse authResponse = authService.verifyEmail(request);
    return ResponseEntity.ok(ApiResponse.success(authResponse));
  }

  @PostMapping("/resend-verification-otp")
  public ResponseEntity<ApiResponse<ResendVerificationOtpResponse>> resendVerificationOtp(
      @Valid @RequestBody ResendVerificationOtpRequest request) {

    ResendVerificationOtpResponse response = authService.resendVerificationOtp(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refresh(
      @Valid @RequestBody RefreshRequest request) {

    AuthResponse response = authService.refresh(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<ProfileResponse>> getMe() {

    UUID accountId = SecurityUtils.getCurrentAccountId();

    ProfileResponse response = authService.getMe(accountId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/switch-role")
  public ResponseEntity<ApiResponse<AuthResponse>> switchRole(
      @Valid @RequestBody SwitchRoleRequest request) {

    UUID accountId = SecurityUtils.getCurrentAccountId();
    String currentRole = SecurityUtils.getCurrentRoleName();

    if (currentRole != null && currentRole.equals(request.role())) {
      throw new AppException(ErrorCode.ROLE_ALREADY_IN_USE);
    }

    AuthResponse response = authService.switchRole(accountId, request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/password-reset/send-otp")
  public ResponseEntity<ApiResponse<SendOtpResetPasswordResponse>> sendOtpResetPassword(
      @Valid @RequestBody SendOtpResetPasswordRequest request) {

    SendOtpResetPasswordResponse response = authService.sendOtpResetPassword(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/password-reset/resend-otp")
  public ResponseEntity<ApiResponse<ResendOtpPasswordResetResponse>> resendOtpResetPassword(
      @Valid @RequestBody ResendOtpPasswordResetRequest request) {

    ResendOtpPasswordResetResponse response = authService.resendOtpPasswordReset(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/password-reset/verify")
  public ResponseEntity<ApiResponse<VerifyPasswordResetResponse>> verifyPasswordReset(
      @Valid @RequestBody VerifyPasswordResetRequest request) {

    VerifyPasswordResetResponse response = authService.verifyResetPassword(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/password-reset/reset")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request) {

    authService.resetPassword(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {

    UUID accountId = SecurityUtils.getCurrentAccountId();

    authService.changePassword(accountId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/change-email/send-otp")
  public ResponseEntity<ApiResponse<SendOtpChangeEmailResponse>> sendOtpChangeEmail(
      @Valid @RequestBody SendOtpChangeEmailRequest request) {

    String currentEmail = SecurityUtils.getCurrentEmail();
    SendOtpChangeEmailResponse response = authService.sendOtpChangeEmail(request, currentEmail);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/change-email/resend-otp")
  public ResponseEntity<ApiResponse<ResendOtpChangeEmailResponse>> resendOtpChangeEmail(
      @Valid @RequestBody ResendOtpChangeEmailRequest request) {

    ResendOtpChangeEmailResponse response = authService.resendOtpChangeEmail(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/change-email/verify")
  public ResponseEntity<ApiResponse<AuthResponse>> verifyChangeEmail(
      @Valid @RequestBody VerifyChangeEmailRequest request) {

    String currentEmail = SecurityUtils.getCurrentEmail();
    AuthResponse response = authService.verifyChangeEmail(request, currentEmail);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {

    authService.logout(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/logout-all")
  public ResponseEntity<ApiResponse<Void>> logoutAll() {
    UUID accountId = SecurityUtils.getCurrentAccountId();

    authService.logoutAll(accountId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
