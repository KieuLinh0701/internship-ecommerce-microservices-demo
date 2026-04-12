package com.teamsolution.auth.dto.internal;

import java.util.UUID;

public record RefreshTokenData(UUID accountId, String selectedRole) {}
