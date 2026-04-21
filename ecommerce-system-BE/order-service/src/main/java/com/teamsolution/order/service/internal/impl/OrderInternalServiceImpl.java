package com.teamsolution.order.service.internal.impl;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import com.teamsolution.common.core.enums.order.OrderPaymentStatus;
import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.order.config.properties.OrderProperties;
import com.teamsolution.order.entity.Order;
import com.teamsolution.order.enums.OrderCancelReason;
import com.teamsolution.order.exception.ErrorCode;
import com.teamsolution.order.kafka.producer.OrderProducer;
import com.teamsolution.order.repository.OrderRepository;
import com.teamsolution.order.service.internal.CouponInternalService;
import com.teamsolution.order.service.internal.OrderInternalService;
import com.teamsolution.order.service.internal.OrderStatusHistoryInternalService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderInternalServiceImpl implements OrderInternalService {

  private final OrderProperties orderProperties;
  private final OrderRepository orderRepository;
  private final OrderStatusHistoryInternalService orderStatusHistoryInternalService;
  private final OrderProducer orderProducer;
  private final CouponInternalService couponInternalService;

  @Override
  public Order findByIdAndCustomerId(UUID orderId, UUID customerId) {
    return orderRepository
        .findByIdAndCustomerIdAndIsDeletedFalse(orderId, customerId)
        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
  }

  @Override
  @Transactional
  public void handleInventoryFailed(
      UUID orderId, UUID accountId, UUID accountRoleId, UUID customerId) {
    Order order = findByIdAndCustomerIdOrThrowPermanent(orderId, customerId);

    if (order.getStatus().shouldSkipInventoryFailureHandling()) {
      throw new PermanentException(ErrorCode.ORDER_SKIP_INVENTORY_FAILURE, order.getId());
    }

    OrderStatus status = OrderStatus.CANCELLED;

    if (order.getPaymentStatus() == OrderPaymentStatus.PAID
        && order.getPaymentMethod() != OrderPaymentMethod.COD) {
      log.warn("[Order] Payment already PAID, trigger refund, orderId={}", orderId);

      status = OrderStatus.CANCELLING;
      order.setPaymentStatus(OrderPaymentStatus.REFUNDING);
    }

    order.setStatus(status);

    order.setCancelledAt(LocalDateTime.now());

    orderRepository.save(order);

    if (status == OrderStatus.CANCELLING) {
      orderProducer.publishRefundRequestedEvent(order, accountId, accountRoleId, customerId);
    }

    orderStatusHistoryInternalService.save(order, status);
  }

  @Override
  @Transactional
  public void handleRefundCompleted(UUID orderId, UUID customerId) {

    Order order = findByIdAndCustomerIdOrThrowPermanent(orderId, customerId);

    if (!order.getStatus().canProcessRefund()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_STATUS_FOR_REFUND, order.getId(), order.getStatus());
    }

    if (!order.getPaymentStatus().canTransitionToRefundCompleted()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_PAYMENT_STATUS_FOR_REFUND_COMPLETED,
          order.getId(),
          order.getPaymentStatus());
    }
    order.setStatus(OrderStatus.CANCELLED);
    order.setPaymentStatus(OrderPaymentStatus.REFUND);
    order.setRefundAt(LocalDateTime.now());

    orderRepository.save(order);
    orderStatusHistoryInternalService.save(order, order.getStatus());

    couponInternalService.restoreCouponUsageIfNeeded(order.getOrderCoupons());
  }

  @Override
  @Transactional
  public void handleRefundFailed(UUID orderId, UUID customerId) {

    Order order = findByIdAndCustomerIdOrThrowPermanent(orderId, customerId);

    if (!order.getStatus().canProcessRefund()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_STATUS_FOR_REFUND, order.getId(), order.getStatus());
    }

    if (!order.getPaymentStatus().canTransitionToRefundFailed()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_PAYMENT_STATUS_FOR_REFUND_FAILED,
          order.getId(),
          order.getPaymentStatus());
    }

    order.setPaymentStatus(OrderPaymentStatus.REFUND_FAILED);
    orderRepository.save(order);
  }

  @Override
  public void handlePaymentCompleted(UUID orderId, UUID customerId) {
    Order order = findByIdAndCustomerIdOrThrowPermanent(orderId, customerId);

    if (!order.getStatus().canProcessPay()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_STATUS_FOR_PAYMENT, order.getId(), order.getStatus());
    }

    if (!order.getPaymentStatus().canTransitionToPaid()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_PAYMENT_STATUS_FOR_PAYMENT_COMPLETED,
          order.getId(),
          order.getPaymentStatus());
    }

    order.setStatus(OrderStatus.CONFIRMED);
    order.setPaymentStatus(OrderPaymentStatus.PAID);
    order.setPaidAt(LocalDateTime.now());
    orderRepository.save(order);

    orderProducer.publishOrderConfirmedEvent(order.getId(), order.getOrderItems());
  }

  @Override
  public void handlePaymentFailed(UUID orderId, UUID customerId) {
    Order order = findByIdAndCustomerIdOrThrowPermanent(orderId, customerId);

    if (!order.getStatus().canProcessPay()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_STATUS_FOR_PAYMENT, order.getId(), order.getStatus());
    }

    if (!order.getPaymentStatus().canTransitionToPaid()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_PAYMENT_STATUS_FOR_PAYMENT_FAILED,
          order.getId(),
          order.getPaymentStatus());
    }

    if (!order.getPaymentStatus().canTransitionToRefundFailed()) {
      throw new PermanentException(
          ErrorCode.ORDER_INVALID_PAYMENT_STATUS_FOR_REFUND_FAILED,
          order.getId(),
          order.getPaymentStatus());
    }

    order.setPaymentStatus(OrderPaymentStatus.PAYMENT_FAILED);
    orderRepository.save(order);
  }

  @Override
  @Transactional
  public List<Order> bulkCancelExpiredUnpaidOrders() {
    List<Order> expiredOrders =
        orderRepository.findByStatusAndPaymentMethodNotAndCreatedAtBeforeAndIsDeletedFalse(
            OrderStatus.PENDING,
            OrderPaymentMethod.COD,
            LocalDateTime.now().minusHours(orderProperties.getPaymentTimeoutHours()));

    if (expiredOrders.isEmpty()) {
      return List.of();
    }

    expiredOrders.forEach(
        order -> {
          LocalDateTime cancelledAt =
              order.getCreatedAt().plusHours(orderProperties.getPaymentTimeoutHours());

          order.setStatus(OrderStatus.CANCELLED);
          order.setCancelledAt(cancelledAt);
          order.setCancelReason(OrderCancelReason.PAYMENT_TIMEOUT);
        });

    orderRepository.saveAll(expiredOrders);
    return expiredOrders;
  }

  private Order findByIdAndCustomerIdOrThrowPermanent(UUID orderId, UUID customerId) {
    return orderRepository
        .findByIdAndCustomerIdAndIsDeletedFalse(orderId, customerId)
        .orElseThrow(() -> new PermanentException(ErrorCode.ORDER_NOT_FOUND));
  }
}
