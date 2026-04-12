package com.teamsolution.admin.service.auth;

import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;

public interface RoleService {
  PageResponse<RoleResponse> fetchRoles(FilterRoleRequest filterRequest);

  PageResponse<RoleResponse> fetchActiveRoles();
}
