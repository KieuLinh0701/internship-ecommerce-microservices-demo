package com.teamsolution.common.core.exception;

public class BusinessException
        extends AppException {
    public BusinessException(BaseErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(BaseErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public boolean isRetryable() {
    return true;
  }
}
