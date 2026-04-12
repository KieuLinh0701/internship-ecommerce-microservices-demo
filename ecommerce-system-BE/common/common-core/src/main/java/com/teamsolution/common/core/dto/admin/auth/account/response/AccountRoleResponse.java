package com.teamsolution.common.core.dto.admin.auth.account.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRoleResponse {
  private UUID surrogateId;

  private UUID roleId;
  private String roleName;

  private String status;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private UUID createdBy;
  private UUID updatedBy;

  private Boolean isDeleted;
}
