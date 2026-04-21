package com.teamsolution.order.service.internal;

import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.order.dto.response.OrderStatusHistoryResponse;
import com.teamsolution.order.entity.Order;
import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryInternalService {
  void save(Order order, OrderStatus status);

  List<OrderStatusHistoryResponse> getStatusHistories(UUID orderId);
}
