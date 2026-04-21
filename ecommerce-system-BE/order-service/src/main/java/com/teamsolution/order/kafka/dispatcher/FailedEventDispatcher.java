package com.teamsolution.order.kafka.dispatcher;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.order.kafka.handler.InventoryReservationFailedEventHandler;
import com.teamsolution.order.kafka.handler.OrderPaymentEventHandler;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

  private final InventoryReservationFailedEventHandler inventoryReservationFailedEventHandler;
  private final OrderPaymentEventHandler orderPaymentEventHandler;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void dispatch(String topic, Object event, UUID failedEventId) {
    switch (topic) {
      case KafkaTopics.INVENTORY_RESERVATION_FAILED ->
          inventoryReservationFailedEventHandler.handleRetry(
              (InventoryReservationFailedEvent) event, failedEventId);
      case KafkaTopics.ORDER_PAYMENT ->
          orderPaymentEventHandler.handleRetry((PaymentEvent) event, failedEventId);

      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    }
  }
}
