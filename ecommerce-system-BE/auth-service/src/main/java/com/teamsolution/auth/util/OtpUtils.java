package com.teamsolution.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class OtpUtils {

  private OtpUtils() {}

  public static String hashOtp(String otp) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(encodedhash);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to hash OTP", e);
    }
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
