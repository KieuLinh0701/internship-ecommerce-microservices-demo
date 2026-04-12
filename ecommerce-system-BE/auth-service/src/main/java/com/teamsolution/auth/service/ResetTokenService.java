package com.teamsolution.auth.service;

import java.util.UUID;

public interface ResetTokenService {
  String generate(UUID accountId);

  UUID peek(String rawToken);

  void consume(String rawToken);
}
