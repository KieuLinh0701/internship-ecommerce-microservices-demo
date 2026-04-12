package com.teamsolution.auth.service;

import com.teamsolution.auth.entity.Role;
import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

  Page<RoleResponse> getAll(Pageable pageable, FilterRoleRequest request);

  Role findByNameAndStatus(String name, RoleStatus status);
}
