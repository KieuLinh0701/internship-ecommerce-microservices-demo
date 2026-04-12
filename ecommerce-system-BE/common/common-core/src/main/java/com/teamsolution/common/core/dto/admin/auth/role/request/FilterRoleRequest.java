package com.teamsolution.common.core.dto.admin.auth.role.request;

import com.teamsolution.common.core.dto.common.request.BaseFilterRequest;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterRoleRequest extends BaseFilterRequest {
  private String keyword;
  private RoleStatus status;
  private Boolean isDeleted = false;
}
