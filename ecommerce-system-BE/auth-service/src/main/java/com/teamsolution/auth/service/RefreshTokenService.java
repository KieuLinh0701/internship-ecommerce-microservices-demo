package com.teamsolution.auth.service;

import com.teamsolution.auth.dto.internal.RefreshTokenData;
import java.util.UUID;

public interface RefreshTokenService {
  void save(UUID accountId, String rawRefreshToken, String selectedRole);

  RefreshTokenData validate(String rawRefreshToken);

  void revokeAllRefreshTokensByAccountId(UUID accountId);

  void revoke(String rawRefreshToken);
}
