package com.teamsolution.auth.dto.response;

import com.teamsolution.common.core.enums.auth.AccountStatus;
import java.util.Set;
import java.util.UUID;

public record ProfileResponse(UUID id, String email, AccountStatus status, Set<String> roles) {}
