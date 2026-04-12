package com.teamsolution.admin.client.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.admin.client.BaseInternalClient;
import com.teamsolution.common.core.dto.admin.auth.account.request.CreateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AccountInternalClient extends BaseInternalClient {

  private static final ParameterizedTypeReference<ApiResponse<PageResponse<AccountSummaryResponse>>>
      PAGE_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<ApiResponse<AccountSummaryResponse>>
      SUMMARY_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<ApiResponse<AccountDetailResponse>> DETAIL_TYPE =
      new ParameterizedTypeReference<>() {};

  public AccountInternalClient(RestClient restClient, ObjectMapper objectMapper) {
    super(restClient, objectMapper);
  }

  public PageResponse<AccountSummaryResponse> getAccounts(
      String serviceId, FilterAccountRequest request) {
    return fetchPage(serviceId, "/internal/accounts", request, PAGE_TYPE);
  }

  public AccountDetailResponse getAccountById(String serviceId, UUID accountId) {
    return getAction(serviceId, "/internal/accounts/" + accountId, DETAIL_TYPE);
  }

  public AccountSummaryResponse createAccount(String serviceId, CreateAccountRequest request) {
    return postAction(serviceId, "/internal/accounts/", request, SUMMARY_TYPE);
  }

  public AccountDetailResponse updateAccountById(
      String serviceId, UUID accountId, UpdateAccountRequest request) {
    return patchAction(serviceId, "/internal/accounts/" + accountId, request, DETAIL_TYPE);
  }

  public void deleteAccountById(String serviceId, UUID accountId) {
    deleteAction(serviceId, "/internal/accounts/" + accountId);
  }

  public AccountSummaryResponse updateAccountStatusById(
      String serviceId, UUID accountId, UpdateAccountStatusRequest request) {
    return patchAction(
        serviceId, "/internal/accounts/" + accountId + "/status", request, SUMMARY_TYPE);
  }
}
