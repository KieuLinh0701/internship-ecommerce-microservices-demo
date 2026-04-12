package com.teamsolution.auth.service;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.common.core.enums.auth.TokenType;
import java.util.UUID;

public interface VerificationTokenService {

  UUID verifyOtp(String email, String rawOtp, TokenType type);

  void createOtpFlow(Account account, String eventType, TokenType type);

  void sendOtpFlow(String email, String eventType, TokenType type);

  void createOtpFlowForEmail(String email, String eventType, TokenType type, int resendCount);
}
