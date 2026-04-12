package com.teamsolution.admin.controller.auth;

import com.teamsolution.admin.service.auth.AccountService;
import com.teamsolution.common.core.dto.admin.auth.account.request.CreateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.FilterAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountRequest;
import com.teamsolution.common.core.dto.admin.auth.account.request.UpdateAccountStatusRequest;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountSummaryResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<AccountSummaryResponse>>> getAll(
      @ModelAttribute FilterAccountRequest request) {
    return ResponseEntity.ok(ApiResponse.success(accountService.getAccounts(request)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountDetailResponse>> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(accountService.getAccountById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AccountSummaryResponse>> create(
      @RequestBody CreateAccountRequest request) {
    return ResponseEntity.ok(ApiResponse.success(accountService.createAccount(request)));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountDetailResponse>> updateById(
      @PathVariable UUID id, @RequestBody UpdateAccountRequest request) {
    return ResponseEntity.ok(ApiResponse.success(accountService.updateAccountById(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
    accountService.deleteAccountById(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<ApiResponse<AccountSummaryResponse>> updateStatusById(
      @PathVariable UUID id, @RequestBody UpdateAccountStatusRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(accountService.updateAccountStatusById(id, request)));
  }
}
