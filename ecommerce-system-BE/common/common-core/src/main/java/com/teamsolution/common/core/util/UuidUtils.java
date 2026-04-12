package com.teamsolution.common.core.util;

import com.github.f4b6a3.uuid.UuidCreator;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import java.util.UUID;

public class UuidUtils {

  public static UUID generate() {
    return UuidCreator.getTimeOrderedEpoch();
  }

  public static UUID parse(String value) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new AppException(CommonErrorCode.INVALID_UUID);
    }
  }
}
