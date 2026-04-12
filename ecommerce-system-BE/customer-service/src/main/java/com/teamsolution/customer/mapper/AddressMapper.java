package com.teamsolution.customer.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.customer.dto.response.AddressResponse;
import com.teamsolution.customer.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper extends BaseMapper<Address, AddressResponse> {}
