package com.teamsolution.auth.mapper;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.common.core.dto.admin.auth.account.response.AccountDetailResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {AccountRoleMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountDetailMapper extends BaseMapper<Account, AccountDetailResponse> {}
