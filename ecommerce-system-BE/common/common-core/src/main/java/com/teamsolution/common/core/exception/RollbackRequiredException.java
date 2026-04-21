package com.teamsolution.common.core.exception;

public class RollbackRequiredException
        extends AppException {
    public RollbackRequiredException(BaseErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RollbackRequiredException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public RollbackRequiredException(BaseErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public boolean isRetryable() {
    return true;
  }
}
