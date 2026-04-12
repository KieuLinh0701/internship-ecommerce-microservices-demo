package com.teamsolution.admin.service.auth;

import com.teamsolution.common.core.dto.admin.auth.account.request.CreateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;

public interface AccountService {
  PageResponse<AccountSummaryResponse> getAccounts(FilterAccountRequest filterRequest);

  AccountDetailResponse getAccountById(UUID accountId);

  AccountSummaryResponse createAccount(CreateAccountRequest request);

  AccountDetailResponse updateAccountById(UUID accountId, UpdateAccountRequest request);

  void deleteAccountById(UUID accountId);

  AccountSummaryResponse updateAccountStatusById(
      UUID accountId, UpdateAccountStatusRequest request);
}
