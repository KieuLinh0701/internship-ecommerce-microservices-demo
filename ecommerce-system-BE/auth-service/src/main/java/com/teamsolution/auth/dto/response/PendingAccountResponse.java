package com.teamsolution.auth.dto.response;

public record PendingAccountResponse(long expiresIn) implements LoginResponse, RegisterResponse {}
