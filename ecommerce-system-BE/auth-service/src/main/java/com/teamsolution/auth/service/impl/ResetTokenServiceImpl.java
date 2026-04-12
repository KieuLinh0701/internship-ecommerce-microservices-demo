package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.config.properties.ResetTokenProperties;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.service.ResetTokenService;
import com.teamsolution.auth.util.OtpUtils;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.redis.constant.RedisKeys;
import com.teamsolution.common.redis.service.RedisService;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetTokenServiceImpl implements ResetTokenService {
  private final RedisService redisService;
  private final ResetTokenProperties resetTokenProperties;

  @Override
  public String generate(UUID accountId) {
    String rawToken = UUID.randomUUID().toString();
    String hash = OtpUtils.hashOtp(rawToken);

    redisService.set(
        RedisKeys.resetTokenKey(hash),
        accountId.toString(),
        Duration.ofSeconds(resetTokenProperties.getExpiresInSeconds()));

    return rawToken;
  }

  @Override
  public UUID peek(String rawToken) {
    String hash = OtpUtils.hashOtp(rawToken);

    String accountId =
        redisService
            .get(RedisKeys.resetTokenKey(hash))
            .orElseThrow(() -> new AppException(ErrorCode.RESET_TOKEN_INVALID));

    return UUID.fromString(accountId);
  }

  @Override
  public void consume(String rawToken) {
    String hash = OtpUtils.hashOtp(rawToken);
    redisService.delete(RedisKeys.resetTokenKey(hash));
  }
}
