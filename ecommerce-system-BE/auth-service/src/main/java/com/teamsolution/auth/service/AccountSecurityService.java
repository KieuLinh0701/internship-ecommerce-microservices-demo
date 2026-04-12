package com.teamsolution.auth.service;

import com.teamsolution.auth.entity.Account;
import java.util.UUID;

public interface AccountSecurityService {

  int handleFailedAttempt(UUID accountId);

  void resetFailedAttemp(UUID accountId);

  void reactivate(Account account);
}
