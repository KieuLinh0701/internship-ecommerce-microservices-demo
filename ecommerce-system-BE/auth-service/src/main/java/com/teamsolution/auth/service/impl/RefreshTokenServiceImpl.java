package com.teamsolution.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.auth.config.properties.JwtProperties;
import com.teamsolution.auth.dto.internal.RefreshTokenData;
import com.teamsolution.auth.dto.internal.RefreshTokenValue;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.service.RefreshTokenService;
import com.teamsolution.auth.util.OtpUtils;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.redis.constant.RedisKeys;
import com.teamsolution.common.redis.service.RedisService;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private final RedisService redisService;
  private final JwtProperties jwtProperties;
  private final ObjectMapper objectMapper;

  @Override
  public void save(UUID accountId, String rawRefreshToken, String selectedRole) {
    String hashRefreshToken = OtpUtils.hashOtp(rawRefreshToken);
    Duration ttl = Duration.ofSeconds(jwtProperties.getRefreshTokenExpiration());

    RefreshTokenValue value =
        RefreshTokenValue.builder()
            .accountId(accountId)
            .selectedRole(selectedRole)
            .issuedAt(Instant.now().getEpochSecond())
            .build();

    String tokenKey = RedisKeys.refreshTokenKey(hashRefreshToken);
    redisService.set(tokenKey, serialize(value), ttl);

    String accountKey = RedisKeys.refreshTokenAccountKey(accountId);
    redisService.addToSet(accountKey, hashRefreshToken, ttl);
  }

  @Override
  public RefreshTokenData validate(String rawRefreshToken) {
    String hash = OtpUtils.hashOtp(rawRefreshToken);
    String tokenKey = RedisKeys.refreshTokenKey(hash);

    String raw =
        redisService
            .getAndDelete(tokenKey)
            .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_INVALID));

    RefreshTokenValue value = deserialize(raw);

    redisService.removeFromSet(RedisKeys.refreshTokenAccountKey(value.accountId()), hash);

    return new RefreshTokenData(value.accountId(), value.selectedRole());
  }

  @Override
  public void revokeAllRefreshTokensByAccountId(UUID accountId) {
    String accountKey = RedisKeys.refreshTokenAccountKey(accountId);

    Set<String> hashes = redisService.getSet(accountKey);
    if (hashes != null) {
      hashes.forEach(hash -> redisService.delete(RedisKeys.refreshTokenKey(hash)));
    }

    redisService.delete(accountKey);
  }

  private String serialize(RefreshTokenValue value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void revoke(String rawRefreshToken) {
    String hash = OtpUtils.hashOtp(rawRefreshToken);
    String tokenKey = RedisKeys.refreshTokenKey(hash);

    String raw =
        redisService
            .get(tokenKey)
            .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_INVALID));

    RefreshTokenValue value = deserialize(raw);

    redisService.delete(tokenKey);

    redisService.removeFromSet(RedisKeys.refreshTokenAccountKey(value.accountId()), hash);
  }

  private RefreshTokenValue deserialize(String raw) {
    try {
      return objectMapper.readValue(raw, RefreshTokenValue.class);
    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
    }
  }
}
