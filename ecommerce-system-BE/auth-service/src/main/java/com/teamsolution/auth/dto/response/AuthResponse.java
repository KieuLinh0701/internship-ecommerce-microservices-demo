package com.teamsolution.auth.dto.response;

public record AuthResponse(String accessToken, String refreshToken)
    implements LoginResponse, RegisterResponse {}
