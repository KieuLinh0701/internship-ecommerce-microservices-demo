package com.teamsolution.auth.controller.internal;

import com.teamsolution.auth.service.AccountService;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.common.jpa.utils.PageableUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/accounts")
@RequiredArgsConstructor
public class AccountInternalController {

  private final AccountService accountService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<AccountSummaryResponse>>> getAll(
      @ModelAttribute FilterAccountRequest filterRequest) {

    Pageable pageable =
        PageableUtils.toPageable(
            filterRequest.getPage(),
            filterRequest.getSize(),
            filterRequest.getSortBy(),
            filterRequest.getDirection(),
            false);

    Page<AccountSummaryResponse> responsePage = accountService.getAccounts(pageable, filterRequest);
    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(responsePage)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountSummaryResponse>> getById(@PathVariable UUID id) {

    AccountDetailResponse detailResponse = accountService.getAccountById(id);
    return ResponseEntity.ok(ApiResponse.success(detailResponse));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<ApiResponse<Void>> updateStatusById(
      @PathVariable UUID id, @RequestBody UpdateAccountStatusRequest request) {

    UUID currentAdminAccountId = SecurityUtils.getCurrentAccountId();

    accountService.updateAccountStatusById(id, request, currentAdminAccountId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
