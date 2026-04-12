package com.teamsolution.auth.dto.internal;

import java.util.UUID;

public record OtpValue(
    String hashedOtp, UUID accountId, int attempts, int resendCount, long lastSentAt) {}
