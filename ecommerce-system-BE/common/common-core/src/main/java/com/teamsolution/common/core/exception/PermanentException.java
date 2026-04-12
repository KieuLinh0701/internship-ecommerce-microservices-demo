package com.teamsolution.common.core.exception;

public class PermanentException extends AppException {

  public PermanentException(BaseErrorCode errorCode) {
    super(errorCode);
  }

  public PermanentException(BaseErrorCode errorCode, Object... args) {
    super(errorCode, args);
  }

  public boolean isRetryable() {
    return true;
  }
}
