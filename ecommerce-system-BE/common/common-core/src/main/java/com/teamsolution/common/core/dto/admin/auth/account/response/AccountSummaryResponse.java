package com.teamsolution.common.core.dto.admin.auth.account.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AccountSummaryResponse {
  private UUID id;
  private String email;
  private String status;
  private LocalDateTime lastLoginAt;
  private LocalDateTime createdAt;
}
