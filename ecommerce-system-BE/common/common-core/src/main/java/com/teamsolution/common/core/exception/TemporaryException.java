package com.teamsolution.common.core.exception;

public class TemporaryException extends AppException {
  public TemporaryException(BaseErrorCode errorCode) {
    super(errorCode);
  }

  public TemporaryException(BaseErrorCode errorCode, Object... args) {
    super(errorCode, args);
  }

  public boolean isRetryable() {
    return true;
  }
}
