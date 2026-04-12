package com.teamsolution.admin.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {
  UNKNOWN_SERVICE_ID(HttpStatus.NOT_FOUND, "Unknown serviceId: %s"),
  UPSTREAM_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "Service %s returned error: %s"),
  UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "No authenticated account in security context"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
