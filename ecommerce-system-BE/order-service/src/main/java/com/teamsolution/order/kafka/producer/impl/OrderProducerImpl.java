package com.teamsolution.order.kafka.producer.impl;

import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.common.kafka.event.order.OrderConfirmedEvent;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.event.order.OrderItemEvent;
import com.teamsolution.common.kafka.event.order.OrderPaymentTimeoutEvent;
import com.teamsolution.common.kafka.event.order.OrderRefundRequestedEvent;
import com.teamsolution.common.tracing.context.TraceContext;
import com.teamsolution.order.entity.Order;
import com.teamsolution.order.entity.OrderItem;
import com.teamsolution.order.entity.OutboxEvent;
import com.teamsolution.order.enums.EntityName;
import com.teamsolution.order.enums.OrderEventType;
import com.teamsolution.order.kafka.producer.OrderProducer;
import com.teamsolution.order.repository.OutboxEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducerImpl implements OrderProducer {

  private final OutboxEventRepository outboxEventRepository;
  private final TraceContext traceContext;

  @Override
  public void publishOrderCreatedEvent(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      String email,
      UUID orderId,
      String orderNumber,
      List<OrderItem> items,
      List<UUID> cartItemIds) {

    OrderCreatedEvent event =
        OrderCreatedEvent.builder()
            .accountId(accountId)
            .accountRoleId(accountRoleId)
            .customerId(customerId)
            .email(email)
            .orderId(orderId)
            .orderNumber(orderNumber)
            .items(this.createOrderItemEvents(items))
            .cartItemIds(cartItemIds)
            .build();

    this.saveToOutbox(orderId, EntityName.ORDER, OrderEventType.ORDER_CREATED, event);
  }

  @Override
  public void publishRefundRequestedEvent(
      Order order, UUID accountId, UUID accountRoleId, UUID customerId) {
    OrderRefundRequestedEvent event =
        OrderRefundRequestedEvent.builder()
            .accountRoleId(accountRoleId)
            .accountId(accountId)
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .customerId(customerId)
            .build();

    this.saveToOutbox(
        order.getId(), EntityName.ORDER, OrderEventType.ORDER_REFUND_REQUESTED, event);
  }

  @Override
  public void publishOrderConfirmedEvent(UUID orderId, List<OrderItem> items) {
    OrderConfirmedEvent event =
        OrderConfirmedEvent.builder()
            .orderId(orderId)
            .items(this.createOrderItemEvents(items))
            .build();

    this.saveToOutbox(orderId, EntityName.ORDER, OrderEventType.ORDER_CONFIRMED, event);
  }

  @Override
  public void publishOrderCancelledEvent(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      UUID orderId,
      String orderNumber,
      OrderStatus status,
      boolean needRefund,
      List<OrderItem> items) {
    OrderCancelledEvent event =
        OrderCancelledEvent.builder()
            .accountId(accountId)
            .accountRoleId(accountRoleId)
            .customerId(customerId)
            .orderId(orderId)
            .orderNumber(orderNumber)
            .previousStatus(status.name())
            .needRefund(needRefund)
            .items(this.createOrderItemEvents(items))
            .build();

    this.saveToOutbox(orderId, EntityName.ORDER, OrderEventType.ORDER_CANCELLED, event);
  }

  @Override
  public void publishOrderPaymentTimeoutEvent(List<Order> orders) {
    orders.forEach(
        order -> {
          OrderPaymentTimeoutEvent event =
              OrderPaymentTimeoutEvent.builder()
                  .orderId(order.getId())
                  .orderNumber(order.getOrderNumber())
                  .items(this.createOrderItemEvents(order.getOrderItems()))
                  .build();

          this.saveToOutbox(
              order.getId(), EntityName.ORDER, OrderEventType.ORDER_PAYMENT_TIMEOUT, event);
        });
  }

  private List<OrderItemEvent> createOrderItemEvents(List<OrderItem> items) {
    return items.stream()
        .map(item -> new OrderItemEvent(item.getVariantId(), item.getQuantity()))
        .toList();
  }

  private void saveToOutbox(
      UUID aggregateId, EntityName aggregateType, OrderEventType eventType, Object payloadEvent) {

    OutboxEvent outboxEvent =
        OutboxEvent.builder()
            .traceId(traceContext.currentTraceId())
            .aggregateId(aggregateId)
            .aggregateType(aggregateType.getValue())
            .eventType(eventType.name())
            .nextRetryAt(LocalDateTime.now())
            .payload(JsonUtils.toJson(payloadEvent))
            .build();

    outboxEventRepository.save(outboxEvent);
  }
}
