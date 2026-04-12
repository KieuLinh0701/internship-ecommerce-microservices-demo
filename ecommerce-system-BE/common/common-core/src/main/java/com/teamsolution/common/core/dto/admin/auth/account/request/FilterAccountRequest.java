package com.teamsolution.common.core.dto.admin.auth.account.request;

import com.teamsolution.common.core.dto.common.request.BaseFilterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterAccountRequest extends BaseFilterRequest {
  private String keyword;
  private String status;
  private String role;
  private Boolean isDeleted = false;
}
