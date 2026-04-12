package com.teamsolution.common.redis.constant;

import java.util.UUID;

public class RedisKeys {

  private RedisKeys() {}

  public static String lockedKey(String accountId) {
    return "locked:" + accountId;
  }

  public static String suspendedKey(String accountId) {
    return "suspended:" + accountId;
  }

  public static String refreshTokenKey(String accountId) {
    return "refresh:token:" + accountId;
  }

  public static String refreshTokenAccountKey(UUID accountId) {
    return "refresh:account:" + accountId;
  }

  public static String otpKey(String email, String tokenType) {
    return "otp:" + tokenType.toLowerCase() + ":" + email.toLowerCase();
  }

  public static String resetTokenKey(String hash) {
    return "reset:token:" + hash;
  }
}
