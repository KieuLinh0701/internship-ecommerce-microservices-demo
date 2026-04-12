package com.teamsolution.auth.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

  // Account
  ACCOUNT_NOT_PENDING(HttpStatus.BAD_REQUEST, "Only Pending accounts can be activated"),
  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found"),
  ACCOUNT_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email already exists"),
  ACCOUNT_HAS_NO_ACTIVE_ROLES(HttpStatus.NOT_FOUND, "Account has no active roles"),
  ACCOUNT_STATUS_INVALID(HttpStatus.BAD_REQUEST, "Invalid account status"),
  ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account is disabled or deleted"),
  ACCOUNT_LOCKED(
      HttpStatus.FORBIDDEN,
      "Your account has been permanently locked due to too many failed login attempts."),
  PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "Old password is incorrect"),
  PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "New password must be different from old password"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
  ACCOUNT_TEMPORARILY_LOCKED(
      HttpStatus.FORBIDDEN,
      "Account temporarily locked after %s failed attempts. Please try again after %s."),
  ACCOUNT_STATUS_NO_CHANGE(HttpStatus.BAD_REQUEST, "Account status is already %s"),
  ACCOUNT_LOCKED_ADMIN(
      HttpStatus.FORBIDDEN,
      "Your account has been locked by administrator. Please contact support."),
  ACCOUNT_LOCKED_FAILED_ATTEMPTS(
      HttpStatus.FORBIDDEN,
      "Your account is locked due to multiple failed attempts. Please reset your password to unlock your account"),
  ACCOUNT_LOCKED_SYSTEM(HttpStatus.FORBIDDEN, "Your account is locked.Please contact support."),
  ACCOUNT_ROLE_REVOKED(
      HttpStatus.FORBIDDEN, "Your account role has been revoked.Please contact support."),
  REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid or used token"),
  RESET_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid or used token"),
  EMAIL_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "New email must be different from old email"),

  // Account Role
  ACCOUNT_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Account Role not found"),

  // Role
  ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found"),
  ROLE_INVALID(HttpStatus.BAD_REQUEST, "Invalid role selected"),
  ROLE_ALREADY_IN_USE(HttpStatus.CONFLICT, "Role is currently assigned to accounts"),
  ROLE_NAME_DUPLICATE(HttpStatus.CONFLICT, "Role name already exists"),
  ROLE_SYSTEM_PROTECTED(HttpStatus.BAD_REQUEST, "Cannot modify system role"),

  // Token
  TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "No active request found"),
  TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Token expired"),
  TOKEN_INVALID_TYPE(HttpStatus.BAD_REQUEST, "Invalid token type"),
  TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid or used token"),
  TOKEN_MISMATCH(HttpStatus.BAD_REQUEST, "Token mismatch"),

  // OTP
  VERIFICATION_SESSION_NOT_FOUND(
      HttpStatus.NOT_FOUND, "Verification session does not exist or has expired"),
  OTP_INCORRECT(HttpStatus.BAD_REQUEST, "Incorrect OTP. You have %s attempts remaining."),
  OTP_MAX_ATTEMPTS_EXCEEDED(
      HttpStatus.BAD_REQUEST,
      "You have exceeded the maximum number of allowed attempts. Please request a new OTP."),
  OTP_COOLDOWN(HttpStatus.TOO_MANY_REQUESTS, "Please wait before resending"),
  OTP_RESEND_MAX_EXCEEDED(
      HttpStatus.TOO_MANY_REQUESTS,
      "Maximum resend attempts reached, please wait for the current OTP to expire"),
  OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP has expired, please request a new one"),

  // Customer gRPC
  CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
  ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Account already exists"),
  CUSTOMER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Customer server unavailable"),

  // Payment
  UNSUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "Unsupported payment method"),
  PAYMENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create payment"),
  INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "Invalid signature"),
  PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
