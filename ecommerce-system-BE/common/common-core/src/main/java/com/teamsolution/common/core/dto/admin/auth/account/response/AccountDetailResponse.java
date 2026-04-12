package com.teamsolution.common.core.dto.admin.auth.account.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AccountDetailResponse extends AccountSummaryResponse {
  private String createdBy;
  private String updatedBy;
  private LocalDateTime updatedAt;
  private int failedLoginAttempts;
  private LocalDateTime lockedUntil;
  private String lockReason;
  private List<AccountRoleResponse> accountRoles;
}
