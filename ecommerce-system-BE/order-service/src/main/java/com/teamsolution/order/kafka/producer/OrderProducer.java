package com.teamsolution.order.kafka.producer;

import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.order.entity.Order;
import com.teamsolution.order.entity.OrderItem;
import java.util.List;
import java.util.UUID;

public interface OrderProducer {

  void publishOrderCreatedEvent(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      String email,
      UUID orderId,
      String orderNumber,
      List<OrderItem> items,
      List<UUID> cartItemIds);

  void publishRefundRequestedEvent(
      Order order, UUID accountId, UUID accountRoleId, UUID customerId);

  void publishOrderConfirmedEvent(UUID orderId, List<OrderItem> items);

  void publishOrderCancelledEvent(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      UUID orderId,
      String orderNumber,
      OrderStatus status,
      boolean needRefund,
      List<OrderItem> items);

  void publishOrderPaymentTimeoutEvent(List<Order> orders);
}
