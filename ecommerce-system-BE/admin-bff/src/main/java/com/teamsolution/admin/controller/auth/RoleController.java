package com.teamsolution.admin.controller.auth;

import com.teamsolution.admin.service.auth.RoleService;
import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getAll(
      @ModelAttribute FilterRoleRequest request) {
    return ResponseEntity.ok(ApiResponse.success(roleService.fetchRoles(request)));
  }

  @GetMapping("/active")
  public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getActive() {
    return ResponseEntity.ok(ApiResponse.success(roleService.fetchActiveRoles()));
  }
}
