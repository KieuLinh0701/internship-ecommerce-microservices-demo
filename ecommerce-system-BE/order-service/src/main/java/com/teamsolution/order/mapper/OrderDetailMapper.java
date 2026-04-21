package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.OrderDetailResponse;
import com.teamsolution.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {OrderItemMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderDetailMapper extends BaseMapper<Order, OrderDetailResponse> {

  @Override
  @Mapping(target = "address.id", source = "addressId")
  @Mapping(target = "address.name", source = "receiverName")
  @Mapping(target = "address.phone", source = "receiverPhone")
  @Mapping(target = "address.cityName", source = "cityName")
  @Mapping(target = "address.wardName", source = "wardName")
  @Mapping(target = "address.detail", source = "addressDetail")
  OrderDetailResponse toDto(Order entity);
}
