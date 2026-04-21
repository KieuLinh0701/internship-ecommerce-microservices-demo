package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.CreateOrderResponse;
import com.teamsolution.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CreateOrderResponseMapper extends BaseMapper<Order, CreateOrderResponse> {

  @Mapping(target = "paymentUrl", source = "paymentUrl")
  CreateOrderResponse toDto(Order order, String paymentUrl);
}