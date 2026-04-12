package com.teamsolution.common.core.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
  HttpStatus getHttpStatus();

  String getMessage();
}
