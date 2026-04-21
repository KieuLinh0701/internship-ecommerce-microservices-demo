package com.teamsolution.inventory.kafka.dispatcher;

import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.common.kafka.event.order.OrderConfirmedEvent;
import com.teamsolution.common.kafka.event.order.OrderPaymentTimeoutEvent;
import com.teamsolution.inventory.kafka.handler.OrderCancelledEventHandler;
import com.teamsolution.inventory.kafka.handler.OrderConfirmedEventHandler;
import com.teamsolution.inventory.kafka.handler.OrderCreatedEventHandler;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.inventory.kafka.handler.OrderPaymentTimeOutEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

  private final OrderCreatedEventHandler orderCreatedEventHandler;
  private final OrderConfirmedEventHandler orderConfirmedEventHandler;
  private final OrderPaymentTimeOutEventHandler orderPaymentTimeOutEventHandler;
  private final OrderCancelledEventHandler orderCancelledEventHandler;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void dispatch(String topic, Object event, UUID failedEventId) {
    switch (topic) {
        case KafkaTopics.ORDER_CREATED ->
                orderCreatedEventHandler.handleRetry((OrderCreatedEvent) event, failedEventId);
        case KafkaTopics.ORDER_CONFIRMED ->
                orderConfirmedEventHandler.handleRetry((OrderConfirmedEvent) event, failedEventId);
        case KafkaTopics.ORDER_PAYMENT_TIMEOUT ->
            orderPaymentTimeOutEventHandler.handleRetry((OrderPaymentTimeoutEvent) event, failedEventId);
        case KafkaTopics.ORDER_CANCELLED ->
                orderCancelledEventHandler.handleRetry((OrderCancelledEvent) event, failedEventId);
      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    }
  }
}
