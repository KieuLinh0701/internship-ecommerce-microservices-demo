package com.teamsolution.common.core.security.principal;

import java.util.UUID;

public record UserPrincipal(
    UUID accountId, UUID accountRoleId, UUID customerId, String email, String currentRoleName) {}
