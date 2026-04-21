package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.OrderItemFeedbackImageResponse;
import com.teamsolution.order.entity.OrderItemFeedbackImage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderItemFeedbackImageMapper
    extends BaseMapper<OrderItemFeedbackImage, OrderItemFeedbackImageResponse> {

  @Named("active")
  default List<OrderItemFeedbackImageResponse> toDtoActiveResponse(
      List<OrderItemFeedbackImage> images) {
    if (images == null) return List.of();
    return images.stream()
        .filter(img -> !Boolean.TRUE.equals(img.getIsDeleted()))
        .map(this::toDto)
        .toList();
  }
}
