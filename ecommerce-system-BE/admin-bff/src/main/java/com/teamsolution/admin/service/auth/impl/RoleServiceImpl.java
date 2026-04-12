package com.teamsolution.admin.service.auth.impl;

import com.teamsolution.admin.client.auth.RoleInternalClient;
import com.teamsolution.admin.config.properties.ConsumerServicesProperties;
import com.teamsolution.admin.enums.ServiceKey;
import com.teamsolution.admin.service.auth.RoleService;
import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final ConsumerServicesProperties consumerServicesProperties;
  private final RoleInternalClient roleInternalClient;

  @Override
  public PageResponse<RoleResponse> fetchRoles(FilterRoleRequest filterRequest) {
    return roleInternalClient.fetchRoles(getServiceId(), filterRequest);
  }

  @Override
  public PageResponse<RoleResponse> fetchActiveRoles() {
    FilterRoleRequest filterRequest = new FilterRoleRequest();
    filterRequest.setStatus(RoleStatus.ACTIVE);
    filterRequest.setIsDeleted(false);
    return roleInternalClient.fetchRoles(getServiceId(), filterRequest);
  }

  private String getServiceId() {
    return consumerServicesProperties.getServiceId(ServiceKey.AUTH);
  }
}
