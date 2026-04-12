package com.teamsolution.auth.service;

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
import java.util.UUID;

public interface AuthService {

  LoginResponse login(LoginRequest request);

  RegisterResponse register(RegisterRequest request);

  AuthResponse verifyEmail(VerifyEmailRequest request);

  ResendVerificationOtpResponse resendVerificationOtp(ResendVerificationOtpRequest request);

  AuthResponse loginWithGoogle(GoogleLoginRequest request);

  AuthResponse refresh(RefreshRequest request);

  AuthResponse switchRole(UUID accountId, SwitchRoleRequest request);

  ProfileResponse getMe(UUID accountId);

  SendOtpResetPasswordResponse sendOtpResetPassword(SendOtpResetPasswordRequest request);

  ResendOtpPasswordResetResponse resendOtpPasswordReset(ResendOtpPasswordResetRequest request);

  VerifyPasswordResetResponse verifyResetPassword(VerifyPasswordResetRequest request);

  void resetPassword(ResetPasswordRequest request);

  void changePassword(UUID accountId, ChangePasswordRequest request);

  SendOtpChangeEmailResponse sendOtpChangeEmail(
      SendOtpChangeEmailRequest request, String currentEmail);

  ResendOtpChangeEmailResponse resendOtpChangeEmail(ResendOtpChangeEmailRequest request);

  AuthResponse verifyChangeEmail(VerifyChangeEmailRequest request, String currentEmail);

  void logout(LogoutRequest request);

  void logoutAll(UUID accountId);
}
