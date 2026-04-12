package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.enums.AccountLockReason;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.grpc.client.CustomerGrpcClient;
import com.teamsolution.auth.mapper.AccountDetailMapper;
import com.teamsolution.auth.mapper.AccountSummaryMapper;
import com.teamsolution.auth.repository.AccountRepository;
import com.teamsolution.auth.service.AccountRoleService;
import com.teamsolution.auth.service.AccountService;
import com.teamsolution.auth.service.RoleService;
import com.teamsolution.auth.specification.AccountSpecification;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import com.teamsolution.common.core.enums.auth.SystemRole;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final AccountRoleService accountRoleService;
  private final RoleService roleService;
  private final CustomerGrpcClient customerGrpcClient;
  private final AccountSummaryMapper accountSummaryMapper;
  private final AccountDetailMapper accountDetailMapper;

  @Override
  public Optional<Account> findByEmail(String email) {
    return accountRepository.findByEmailAndIsDeletedFalse(email);
  }

  @Override
  public Optional<Account> findByEmailWithRoles(String email) {
    return accountRepository.findByEmailWithRoles(email);
  }

  @Override
  public Optional<Account> findByEmailIncludingDeleted(String email) {
    return accountRepository.findByEmail(email);
  }

  @Override
  public Account getAccountWithActiveRolesByAccountId(UUID accountId) {
    return accountRepository
        .findByAccountIdWithActiveRoles(accountId)
        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public Set<String> getActiveRolesByAccountId(UUID accountId) {
    Account account =
        accountRepository
            .findByAccountIdWithActiveRoles(accountId)
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

    if (account.getAccountRoles().isEmpty()) {
      throw new AppException(ErrorCode.ACCOUNT_HAS_NO_ACTIVE_ROLES);
    }

    return account.getAccountRoles().stream()
        .map(ar -> ar.getRole().getName())
        .collect(Collectors.toSet());
  }

  @Override
  public void save(Account account) {
    accountRepository.save(account);
  }

  @Override
  public Account getByIdOrThrow(UUID id) {
    return accountRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Account findOrCreateGoogleAccount(String email, String fullName, String avatarUrl) {
    return accountRepository
        .findByEmail(email)
        .orElseGet(
            () ->
                createNewAccount(
                    email,
                    "",
                    AccountStatus.ACTIVE,
                    AccountRoleStatus.ACTIVE,
                    fullName,
                    "",
                    avatarUrl));
  }

  @Override
  @Transactional
  public Account createNewAccount(
      String email,
      String hashPassword,
      AccountStatus accountStatus,
      AccountRoleStatus accountRoleStatus,
      String fullName,
      String phone,
      String avatarUrl) {
    Account newAccount =
        Account.builder()
            .id(UuidUtils.generate())
            .email(email)
            .password(hashPassword)
            .status(accountStatus)
            .build();
    Account saved = accountRepository.save(newAccount);

    accountRoleService.createCustomerAccountRole(saved, accountRoleStatus, SystemRole.CUSTOMER);

    customerGrpcClient.createNewCustomer(saved.getId(), fullName, phone, avatarUrl);

    return saved;
  }

  // Internal Admin
  @Override
  public Page<AccountSummaryResponse> getAccounts(Pageable pageable, FilterAccountRequest request) {
    Specification<Account> spec =
        Specification.where(AccountSpecification.isDeleted(request.getIsDeleted()))
            .and(AccountSpecification.hasKeyword(request.getKeyword()))
            .and(AccountSpecification.hasStatus(request.getStatus()))
            .and(AccountSpecification.hasRole(request.getRole()));

    return accountRepository.findAll(spec, pageable).map(accountSummaryMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public AccountDetailResponse getAccountById(UUID accountId) {
    Account account = getByIdIncludingDeleted(accountId);
    return accountDetailMapper.toDto(account);
  }

  //    @Override
  //    @Transactional
  //    public void softDeleteAccountById(UUID accountId) {
  //        Account account = findAccountByIdOrThrow(accountId);
  //        account.setStatus(AccountStatus.DELETED);
  //        account.setIsDeleted(true);
  //        accountRepository.save(account);
  //    }

  @Override
  @Transactional
  public void updateAccountStatusById(
      UUID accountId, UpdateAccountStatusRequest request, UUID accountAdminId) {
    Account account = getByIdIncludingDeleted(accountId);

    AccountStatus oldStatus = account.getStatus();
    AccountStatus newStatus = request.status();

    if (oldStatus == newStatus) {
      throw new AppException(ErrorCode.ACCOUNT_STATUS_NO_CHANGE, newStatus.name());
    }

    if (oldStatus.isSuspended()) {
      account.setLockReason(AccountLockReason.NONE);
      account.setFailedLoginAttempts(0);
      account.setLockedUntil(null);
    }

    if (newStatus.isSuspended()) {
      account.setLockReason(AccountLockReason.ADMIN_LOCK);
    }

    account.setStatus(newStatus);
    account.setUpdatedBy(accountAdminId);

    accountRepository.save(account);
  }

  private Account getByIdIncludingDeleted(UUID accountId) {
    return accountRepository
        .findById(accountId)
        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
  }
}
