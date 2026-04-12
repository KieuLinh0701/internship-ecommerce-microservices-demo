package com.teamsolution.auth.mapper;

import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountRoleResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountRoleMapper extends BaseMapper<AccountRole, AccountRoleResponse> {

  @Mapping(target = "roleId", source = "role.id")
  @Mapping(target = "roleName", source = "role.name")
  AccountRoleResponse toDto(AccountRole entity);
}
