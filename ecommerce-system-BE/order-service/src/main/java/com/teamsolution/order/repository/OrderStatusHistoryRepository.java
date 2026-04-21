package com.teamsolution.order.repository;

import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.order.entity.OrderStatusHistory;
import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository extends BaseRepository<OrderStatusHistory, UUID> {
  List<OrderStatusHistory> findByOrderIdOrderByTimeAsc(UUID orderId);

  boolean existsByOrderIdAndStatus(UUID orderId, OrderStatus status);

  List<OrderStatusHistory> findByOrderIdInAndStatus(List<UUID> orderIds, OrderStatus status);
}
