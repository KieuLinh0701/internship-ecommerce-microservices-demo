package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.auth.entity.Role;
import com.teamsolution.auth.entity.id.AccountRoleId;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.repository.AccountRoleRepository;
import com.teamsolution.auth.service.AccountRoleService;
import com.teamsolution.auth.service.RoleService;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import com.teamsolution.common.core.enums.auth.SystemRole;
import com.teamsolution.common.core.exception.AppException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleService {

  private final AccountRoleRepository accountRoleRepository;
  private final RoleService roleService;

  @Override
  public UUID getActiveAccountRoleId(UUID accountId, String roleName) {
    return accountRoleRepository
        .findActiveByAccountIdAndRoleName(accountId, roleName)
        .map(AccountRole::getSurrogateId)
        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_ROLE_NOT_FOUND));
  }

  @Override
  public void save(AccountRole accountRole) {
    accountRoleRepository.save(accountRole);
  }

  @Override
  @Transactional
  public void activateByAccountId(UUID accountId) {
    accountRoleRepository.activateByAccountId(accountId);
  }

  @Override
  public void activateCustomerRole(UUID accountId) {
    accountRoleRepository
        .findByAccount_IdAndStatusAndRole_NameAndIsDeleted(
            accountId, AccountRoleStatus.PENDING, SystemRole.CUSTOMER.name(), false)
        .ifPresent(
            accountRole -> {
              accountRole.setStatus(AccountRoleStatus.ACTIVE);
              accountRoleRepository.save(accountRole);
            });
  }

  @Override
  public void restoreByAccountId(UUID accountId) {
    accountRoleRepository
        .findByAccount_IdAndStatusAndIsDeleted(accountId, AccountRoleStatus.DELETED, true)
        .forEach(
            role -> {
              role.setStatus(AccountRoleStatus.ACTIVE);
              accountRoleRepository.save(role);
            });
  }

  @Override
  public Optional<AccountRole> getByAccountIdAndRoleName(UUID accountId, SystemRole role) {
    return accountRoleRepository.findByAccountIdAndRoleName(accountId, role.name());
  }

  @Override
  public Set<String> getActiveRoleNamesByAccountId(UUID accountId) {
    return accountRoleRepository
        .findActiveRolesByAccountId(accountId, AccountRoleStatus.ACTIVE, RoleStatus.ACTIVE)
        .stream()
        .map(Role::getName)
        .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public void createCustomerAccountRole(
      Account account, AccountRoleStatus accountRoleStatus, SystemRole roleName) {

    Role role = roleService.findByNameAndStatus(roleName.name(), RoleStatus.ACTIVE);

    AccountRole accountRole =
        AccountRole.builder()
            .id(new AccountRoleId(account.getId(), role.getId()))
            .account(account)
            .role(role)
            .status(accountRoleStatus)
            .build();
    accountRoleRepository.save(accountRole);
  }
}
