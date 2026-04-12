package com.teamsolution.auth.service;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

  Page<AccountSummaryResponse> getAccounts(Pageable pageable, FilterAccountRequest request);

  AccountDetailResponse getAccountById(UUID accountId);

  //    void softDeleteAccountById(UUID accountId);

  void updateAccountStatusById(
      UUID accountId, UpdateAccountStatusRequest request, UUID accountAdminId);

  Optional<Account> findByEmail(String email);

  Optional<Account> findByEmailWithRoles(String email);

  Optional<Account> findByEmailIncludingDeleted(String email);

  Account getAccountWithActiveRolesByAccountId(UUID accountId);

  Set<String> getActiveRolesByAccountId(UUID accountId);

  void save(Account account);

  Account getByIdOrThrow(UUID id);

  Account findOrCreateGoogleAccount(String email, String fullName, String avatarUrl);

  Account createNewAccount(
      String email,
      String hashPassword,
      AccountStatus accountStatus,
      AccountRoleStatus accountRoleStatus,
      String fullName,
      String phone,
      String avatarUrl);
}
