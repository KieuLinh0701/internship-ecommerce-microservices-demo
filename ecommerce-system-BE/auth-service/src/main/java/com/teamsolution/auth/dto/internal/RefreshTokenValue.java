package com.teamsolution.auth.dto.internal;

import java.util.UUID;
import lombok.Builder;

@Builder
public record RefreshTokenValue(UUID accountId, String selectedRole, long issuedAt) {}
