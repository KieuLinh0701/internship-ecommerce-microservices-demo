package com.teamsolution.common.core.dto.admin.auth.role.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class RoleResponse {

  private UUID id;
  private String name;
  private String status;
  private Boolean isDeleted;
  private String createdBy;
  private UUID updatedBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
