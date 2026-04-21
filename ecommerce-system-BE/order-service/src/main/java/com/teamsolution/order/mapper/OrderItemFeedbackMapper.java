package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.OrderItemFeedbackResponse;
import com.teamsolution.order.entity.OrderItemFeedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = OrderItemFeedbackImageMapper.class,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderItemFeedbackMapper
    extends BaseMapper<OrderItemFeedback, OrderItemFeedbackResponse> {

  @Mapping(target = "displayName", expression = "java(buildDisplayName(entity, customerName))")
  @Mapping(target = "images", qualifiedByName = "active")
  @Mapping(target = "variantImageUrl", source = "entity.variantImageUrl")
  @Mapping(target = "comment", source = "entity.comment")
  OrderItemFeedbackResponse toDto(OrderItemFeedback entity, String customerName);

  default String buildDisplayName(OrderItemFeedback f, String customerName) {
    if (customerName == null || customerName.isBlank()) return "Anonymous User";

    if (Boolean.TRUE.equals(f.getIsAnonymous())) {
      return maskName(customerName);
    }

    return customerName;
  }

  @Named("maskName")
  default String maskName(String name) {
    if (name == null || name.isEmpty()) return "Anonymous User";

    if (name.length() <= 2) return name.charAt(0) + "*";

    return name.charAt(0) + "***" + name.charAt(name.length() - 1);
  }
}
