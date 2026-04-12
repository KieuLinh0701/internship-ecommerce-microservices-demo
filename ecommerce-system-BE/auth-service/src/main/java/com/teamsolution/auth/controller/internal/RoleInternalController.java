package com.teamsolution.auth.controller.internal;

import com.teamsolution.auth.service.RoleService;
import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.common.jpa.utils.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/roles")
@RequiredArgsConstructor
public class RoleInternalController {

  private final RoleService service;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getAll(
      @ModelAttribute FilterRoleRequest filterRequest) {

    Pageable pageable =
        PageableUtils.toPageable(
            filterRequest.getPage(),
            filterRequest.getSize(),
            filterRequest.getSortBy(),
            filterRequest.getDirection(),
            false);

    Page<RoleResponse> response = service.getAll(pageable, filterRequest);
    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(response)));
  }
}
