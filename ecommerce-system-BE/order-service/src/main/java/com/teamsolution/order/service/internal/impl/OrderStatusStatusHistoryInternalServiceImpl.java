package com.teamsolution.order.service.internal.impl;

import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.order.dto.response.OrderStatusHistoryResponse;
import com.teamsolution.order.entity.Order;
import com.teamsolution.order.entity.OrderStatusHistory;
import com.teamsolution.order.mapper.OrderStatusHistoryMapper;
import com.teamsolution.order.repository.OrderStatusHistoryRepository;
import com.teamsolution.order.service.internal.OrderStatusHistoryInternalService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusStatusHistoryInternalServiceImpl
    implements OrderStatusHistoryInternalService {

  private final OrderStatusHistoryRepository orderStatusHistoryRepository;
  private final OrderStatusHistoryMapper orderStatusHistoryMapper;

  @Override
  public void save(Order order, OrderStatus status) {

    boolean exists = orderStatusHistoryRepository.existsByOrderIdAndStatus(order.getId(), status);

    if (exists) return;

    orderStatusHistoryRepository.save(
        OrderStatusHistory.builder().order(order).status(status).build());
  }

  @Override
  public List<OrderStatusHistoryResponse> getStatusHistories(UUID orderId) {
    return orderStatusHistoryMapper.toDtoList(
        orderStatusHistoryRepository.findByOrderIdOrderByTimeAsc(orderId));
  }
}
