package com.teamsolution.admin.service.auth.impl;

import com.teamsolution.admin.client.auth.AccountInternalClient;
import com.teamsolution.admin.config.properties.ConsumerServicesProperties;
import com.teamsolution.admin.enums.ServiceKey;
import com.teamsolution.admin.service.auth.AccountService;
import com.teamsolution.common.core.dto.admin.auth.account.request.CreateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ConsumerServicesProperties consumerServicesProperties;
  private final AccountInternalClient accountInternalClient;

  @Override
  public PageResponse<AccountSummaryResponse> getAccounts(FilterAccountRequest filterRequest) {
    return accountInternalClient.getAccounts(getServiceId(), filterRequest);
  }

  @Override
  public AccountDetailResponse getAccountById(UUID accountId) {
    return accountInternalClient.getAccountById(getServiceId(), accountId);
  }

  @Override
  public AccountSummaryResponse createAccount(CreateAccountRequest request) {
    return accountInternalClient.createAccount(getServiceId(), request);
  }

  @Override
  public AccountDetailResponse updateAccountById(UUID accountId, UpdateAccountRequest request) {
    return accountInternalClient.updateAccountById(getServiceId(), accountId, request);
  }

  @Override
  public void deleteAccountById(UUID accountId) {
    accountInternalClient.deleteAccountById(getServiceId(), accountId);
  }

  @Override
  public AccountSummaryResponse updateAccountStatusById(
      UUID accountId, UpdateAccountStatusRequest request) {
    return accountInternalClient.updateAccountStatusById(getServiceId(), accountId, request);
  }

  private String getServiceId() {
    return consumerServicesProperties.getServiceId(ServiceKey.AUTH);
  }
}
