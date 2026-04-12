package com.teamsolution.auth.service;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.common.core.enums.auth.SystemRole;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AccountRoleService {

  UUID getActiveAccountRoleId(UUID accountId, String roleName);

  void save(AccountRole accountRole);

  Set<String> getActiveRoleNamesByAccountId(UUID accountId);

  void activateByAccountId(UUID accountId);

  void activateCustomerRole(UUID accountId);

  void restoreByAccountId(UUID accountId);

  Optional<AccountRole> getByAccountIdAndRoleName(UUID accountId, SystemRole role);

  void createCustomerAccountRole(
      Account account, AccountRoleStatus accountRoleStatus, SystemRole roleName);
}
