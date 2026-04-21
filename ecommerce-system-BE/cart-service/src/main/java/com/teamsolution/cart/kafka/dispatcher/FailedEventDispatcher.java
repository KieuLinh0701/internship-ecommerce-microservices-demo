package com.teamsolution.cart.kafka.dispatcher;

import com.teamsolution.cart.kafka.handler.InventoryReservationFailedEventHandler;
import com.teamsolution.cart.kafka.handler.OrderCreatedEventHandler;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

    private final OrderCreatedEventHandler orderCreatedEventHandler;
    private final InventoryReservationFailedEventHandler inventoryReservationFailedEventHandler;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(String topic, Object event, UUID failedEventId) {
        switch (topic) {
            case KafkaTopics.ORDER_CREATED ->
                    orderCreatedEventHandler.handleRetry(
                            (OrderCreatedEvent) event,
                            failedEventId);
            case KafkaTopics.INVENTORY_RESERVATION_FAILED ->
                    inventoryReservationFailedEventHandler.handleRetry(
                            (InventoryReservationFailedEvent) event,
                            failedEventId);
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        }
    }
}
