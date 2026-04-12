package com.teamsolution.notification.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found"),
  EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email"),
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
