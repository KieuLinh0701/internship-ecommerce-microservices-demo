package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.config.properties.LoginSecurityProperties;
import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.enums.AccountLockReason;
import com.teamsolution.auth.repository.AccountRepository;
import com.teamsolution.auth.service.AccountSecurityService;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountSecurityServiceImpl implements AccountSecurityService {

  private final AccountRepository accountRepository;
  private final LoginSecurityProperties loginSecurityProperties;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int handleFailedAttempt(UUID accountId) {
    Account account = accountRepository.findById(accountId).orElseThrow();

    int attempts = account.getFailedLoginAttempts() + 1;
    account.setFailedLoginAttempts(attempts);

    if (attempts >= loginSecurityProperties.getMaxAttemptsPermanent()) {
      account.setStatus(AccountStatus.SUSPENDED);
      account.setLockReason(AccountLockReason.FAILED_ATTEMPTS);
      account.setLockedUntil(null);
    } else if (attempts >= loginSecurityProperties.getMaxAttempts()) {
      account.setLockedUntil(
          LocalDateTime.now().plusMinutes(loginSecurityProperties.getLockDurationMinutes()));
    }

    accountRepository.saveAndFlush(account);

    return attempts;
  }

  @Override
  public void resetFailedAttemp(UUID accountId) {

    Account account = accountRepository.findById(accountId).orElseThrow();

    System.out.println("h");
    if (account.getFailedLoginAttempts() > 0 || account.getLockedUntil() != null) {
      account.setFailedLoginAttempts(0);
      account.setLockedUntil(null);
      accountRepository.saveAndFlush(account);
    }
  }

  @Override
  @Transactional
  public void reactivate(Account account) {
    account.setStatus(AccountStatus.ACTIVE);
    account.setUpdatedBy(account.getId());

    accountRepository.save(account);
  }
}
