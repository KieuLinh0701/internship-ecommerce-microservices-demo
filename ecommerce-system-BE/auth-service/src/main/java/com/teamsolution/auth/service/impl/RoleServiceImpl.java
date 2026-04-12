package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.entity.Role;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.mapper.RoleMapper;
import com.teamsolution.auth.repository.RoleRepository;
import com.teamsolution.auth.service.RoleService;
import com.teamsolution.auth.specification.RoleSpecification;
import com.teamsolution.common.core.dto.admin.auth.role.request.FilterRoleRequest;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import com.teamsolution.common.core.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;

  @Override
  public Page<RoleResponse> getAll(Pageable pageable, FilterRoleRequest request) {
    Specification<Role> spec =
        Specification.where(RoleSpecification.isDeleted(request.getIsDeleted()))
            .and(RoleSpecification.hasName(request.getKeyword()))
            .and(RoleSpecification.hasStatus(request.getStatus()));

    return roleRepository.findAll(spec, pageable).map(roleMapper::toDto);
  }

  @Override
  public Role findByNameAndStatus(String name, RoleStatus status) {
    return roleRepository
        .findByNameAndStatusAndIsDeletedFalse(name, status)
        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
  }
}
