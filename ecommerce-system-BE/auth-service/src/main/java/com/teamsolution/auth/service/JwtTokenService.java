package com.teamsolution.auth.service;

import com.teamsolution.auth.dto.response.AuthResponse;
import java.util.UUID;

public interface JwtTokenService {
  AuthResponse generateTokens(UUID accountId, String selectedRole);
}
