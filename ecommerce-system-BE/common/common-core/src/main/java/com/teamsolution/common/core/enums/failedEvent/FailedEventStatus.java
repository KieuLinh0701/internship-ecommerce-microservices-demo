package com.teamsolution.common.core.enums.failedEvent;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;

public enum FailedEventStatus {
  FAILED,
  RETRYING,
  SUCCESS,
  DEAD;

  public void validateCanRetry() {
    switch (this) {
      case SUCCESS -> {
        throw new AppException(CommonErrorCode.FAILED_EVENT_ALREADY_SUCCESS);
      }
      case RETRYING -> {
        throw new AppException(CommonErrorCode.FAILED_EVENT_IS_RETRYING);
      }
      case DEAD -> {
        throw new AppException(CommonErrorCode.FAILED_EVENT_ALREADY_DEAD);
      }
      case FAILED -> {}
      default -> throw new AppException(CommonErrorCode.UNEXPECTED_ENUM_VALUE);
    }
  }

  public void validateCanIgnore() {
    switch (this) {
      case SUCCESS -> {
        throw new AppException(CommonErrorCode.FAILED_EVENT_ALREADY_SUCCESS);
      }
      case RETRYING -> {
        throw new AppException(CommonErrorCode.FAILED_EVENT_IS_RETRYING);
      }
      case DEAD -> {
        throw new AppException(CommonErrorCode.FAILED_EVENT_ALREADY_DEAD);
      }
      case FAILED -> {}
      default -> throw new AppException(CommonErrorCode.UNEXPECTED_ENUM_VALUE);
    }
  }

  public boolean isSuccess() {
    return this == SUCCESS;
  }
}
