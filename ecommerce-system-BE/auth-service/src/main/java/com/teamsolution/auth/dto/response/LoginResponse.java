package com.teamsolution.auth.dto.response;

public sealed interface LoginResponse permits AuthResponse, PendingAccountResponse {}
