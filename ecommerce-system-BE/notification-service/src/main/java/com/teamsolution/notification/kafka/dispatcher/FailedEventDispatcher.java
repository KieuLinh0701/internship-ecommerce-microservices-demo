package com.teamsolution.notification.kafka.dispatcher;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.common.kafka.event.order.OrderCancelRequestedEvent;
import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.notification.kafka.handler.AuthNotificationEventHandler;
import com.teamsolution.notification.kafka.handler.InventoryReservationFailedEventHandler;
import com.teamsolution.notification.kafka.handler.OrderCancelRequestedEventHandler;
import com.teamsolution.notification.kafka.handler.OrderPaymentEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

    private final AuthNotificationEventHandler authNotificationEventHandler;
    private final InventoryReservationFailedEventHandler inventoryReservationFailedEventHandler;
    private final OrderCancelRequestedEventHandler orderCancelRequestedEventHandler;
    private final OrderPaymentEventHandler orderPaymentEventHandler;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(String topic, Object event, UUID failedEventId) {
        switch (topic) {
            case KafkaTopics.AUTH_NOTIFICATION ->
                    authNotificationEventHandler.handleRetry((AuthNotificationEvent) event, failedEventId);
            case KafkaTopics.INVENTORY_RESERVATION_FAILED ->
                    inventoryReservationFailedEventHandler.handleRetry((InventoryReservationFailedEvent) event,
                            failedEventId);
            case KafkaTopics.ORDER_CANCEL_REQUESTED ->
                orderCancelRequestedEventHandler.handleRetry((OrderCancelRequestedEvent) event, failedEventId);
            case KafkaTopics.ORDER_PAYMENT ->
                    orderPaymentEventHandler.handleRetry((PaymentEvent) event, failedEventId);
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        }
    }
}