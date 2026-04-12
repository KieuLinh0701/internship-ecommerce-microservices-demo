package com.teamsolution.admin.client.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.admin.client.BaseInternalClient;
import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RoleInternalClient extends BaseInternalClient {

  private static final ParameterizedTypeReference<ApiResponse<PageResponse<RoleResponse>>>
      ROLE_PAGE = new ParameterizedTypeReference<>() {};

  public RoleInternalClient(RestClient restClient, ObjectMapper objectMapper) {
    super(restClient, objectMapper);
  }

  public PageResponse<RoleResponse> fetchRoles(String serviceId, FilterRoleRequest request) {
    return fetchPage(serviceId, "/internal/roles", request, ROLE_PAGE);
  }
}
