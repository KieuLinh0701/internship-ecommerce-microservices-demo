package com.teamsolution.auth.mapper;

import com.teamsolution.auth.entity.Role;
import com.teamsolution.common.core.dto.admin.auth.role.response.RoleResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper extends BaseMapper<Role, RoleResponse> {}
