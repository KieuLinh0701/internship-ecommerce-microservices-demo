package com.teamsolution.customer.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.customer.dto.response.CustomerResponse;
import com.teamsolution.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper extends BaseMapper<Customer, CustomerResponse> {}
