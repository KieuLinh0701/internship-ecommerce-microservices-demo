package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.OrderItemResponse;
import com.teamsolution.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemResponse> {

  @Override
  @Mapping(target = "product.id", source = "productId")
  @Mapping(target = "product.name", source = "productName")
  @Mapping(target = "variant.name", source = "variantName")
  @Mapping(target = "variant.imageUrl", source = "variantImageUrl")
  OrderItemResponse toDto(OrderItem entity);
}
