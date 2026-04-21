package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.OrderStatusHistoryResponse;
import com.teamsolution.order.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderStatusHistoryMapper
    extends BaseMapper<OrderStatusHistory, OrderStatusHistoryResponse> {}
