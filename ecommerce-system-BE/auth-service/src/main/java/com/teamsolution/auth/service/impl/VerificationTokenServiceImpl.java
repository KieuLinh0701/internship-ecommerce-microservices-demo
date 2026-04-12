package com.teamsolution.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.auth.config.properties.OtpSecurityProperties;
import com.teamsolution.auth.dto.internal.OtpValue;
import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.enums.AuthEventType;
import com.teamsolution.auth.enums.EntityName;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.kafka.producer.NotificationProducer;
import com.teamsolution.auth.service.VerificationTokenService;
import com.teamsolution.auth.util.OtpUtils;
import com.teamsolution.common.core.enums.auth.TokenType;
import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.constant.NotificationVariables;
import com.teamsolution.common.redis.constant.RedisKeys;
import com.teamsolution.common.redis.service.RedisService;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

  private final RedisService redisService;
  private final ObjectMapper objectMapper;
  private final NotificationProducer notificationProducer;

  private final OtpSecurityProperties otpSecurityProperties;

  @Override
  public UUID verifyOtp(String email, String rawOtp, TokenType type) {
    String key = RedisKeys.otpKey(email, type.name());

    OtpValue value = getOrThrow(key);

    long secondsSinceLastSent = (System.currentTimeMillis() - value.lastSentAt()) / 1000;
    if (secondsSinceLastSent > otpSecurityProperties.getExpiresInSeconds()) {
      throw new AppException(ErrorCode.OTP_EXPIRED);
    }

    if (value.hashedOtp().equals(OtpUtils.hashOtp(rawOtp))) {
      redisService.delete(key);
      return value.accountId();
    }

    int newAttempts = value.attempts() + 1;
    Duration remainingTtl = redisService.getExpire(key);

    OtpValue newValue =
        new OtpValue(
            value.hashedOtp(),
            value.accountId(),
            newAttempts,
            value.resendCount(),
            value.lastSentAt());
    save(key, newValue, remainingTtl);

    if (newAttempts >= otpSecurityProperties.getMaxAttempts()) {
      throw new AppException(ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED);
    }

    throw new AppException(
        ErrorCode.OTP_INCORRECT, otpSecurityProperties.getMaxAttempts() - newAttempts);
  }

  @Override
  public void createOtpFlow(Account account, String eventType, TokenType type) {
    createAndSendToken(account, eventType, type, 0);
  }

  @Override
  public void sendOtpFlow(String email, String eventType, TokenType type) {

    String key = RedisKeys.otpKey(email, type.name());

    OtpValue value = getOrThrow(key);

    validateResendLimits(value.lastSentAt(), value.resendCount());

    redisService.delete(key);

    createOtpFlowForEmail(email, eventType, type, value.resendCount() + 1);
  }

  @Override
  public void createOtpFlowForEmail(
      String email, String eventType, TokenType type, int resendCount) {

    String rawOtp = RandomStringUtils.randomNumeric(6);
    String key = RedisKeys.otpKey(email, type.name());

    OtpValue value =
        new OtpValue(
            OtpUtils.hashOtp(rawOtp),
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            0,
            resendCount,
            System.currentTimeMillis());

    Duration ttl = Duration.ofSeconds(otpSecurityProperties.getSessionExpiresInSeconds());
    save(key, value, ttl);

    notificationProducer.send(
        email,
        eventType,
        List.of(NotificationChannel.EMAIL),
        Map.of(
            NotificationVariables.OTP,
            rawOtp,
            NotificationVariables.EXPIRED_IN,
            String.valueOf(otpSecurityProperties.getExpiresInSeconds() / 60)),
        AuthEventType.OTP_SENT_FOR_CHANGE_EMAIL,
        EntityName.ACCOUNT);
  }

  private void validateResendLimits(long lastSentAt, int resendCount) {
    long now = System.currentTimeMillis();
    long secondsSinceLastSent = (now - lastSentAt) / 1000;

    if (secondsSinceLastSent < otpSecurityProperties.getCooldownSeconds()) {
      throw new AppException(
          ErrorCode.OTP_COOLDOWN,
          otpSecurityProperties.getCooldownSeconds() - secondsSinceLastSent);
    }

    if (resendCount >= otpSecurityProperties.getMaxResend()) {
      throw new AppException(ErrorCode.OTP_RESEND_MAX_EXCEEDED);
    }
  }

  private void createAndSendToken(
      Account account, String eventType, TokenType type, int currentResendCount) {
    String rawOtp = RandomStringUtils.randomNumeric(6);
    String key = RedisKeys.otpKey(account.getEmail(), type.name());

    OtpValue value =
        new OtpValue(
            OtpUtils.hashOtp(rawOtp),
            account.getId(),
            0,
            currentResendCount,
            System.currentTimeMillis());

    Duration sessionTtl = Duration.ofSeconds(otpSecurityProperties.getSessionExpiresInSeconds());

    save(key, value, sessionTtl);

    notificationProducer.send(
        account,
        eventType,
        List.of(NotificationChannel.EMAIL),
        Map.of(
            NotificationVariables.OTP,
            rawOtp,
            NotificationVariables.EXPIRED_IN,
            String.valueOf(otpSecurityProperties.getExpiresInSeconds() / 60)),
        AuthEventType.OTP_SENT_FOR_PENDING_LOGIN,
        EntityName.ACCOUNT);
  }

  private OtpValue getOrThrow(String key) {
    String raw =
        redisService
            .get(key)
            .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_SESSION_NOT_FOUND));
    try {
      return objectMapper.readValue(raw, OtpValue.class);
    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.VERIFICATION_SESSION_NOT_FOUND);
    }
  }

  private void save(String key, OtpValue record, Duration ttl) {
    try {
      redisService.set(key, objectMapper.writeValueAsString(record), ttl);
    } catch (JsonProcessingException e) {
      throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
