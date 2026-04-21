package com.teamsolution.notification.kafka.handler;

import com.teamsolution.common.kafka.constant.NotificationEventType;
import com.teamsolution.common.kafka.constant.NotificationVariables;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.notification.service.FailedEventSaverService;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservationFailedEventHandler {

    private final NotificationDispatcher notificationDispatcher;
    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;

    @Transactional
    public void handleRetry(InventoryReservationFailedEvent event, UUID failedEventId) {
        log.info(
                "[Notification][Retry][InventoryReservationFailedEvent] Received event id={}, orderId={}",
                event.getId(),
                event.getOrderId());

        Map<String, String> variables = Map.of(
                NotificationVariables.ORDER_NUMBER, event.getOrderNumber());

        notificationDispatcher.send(
                event.getAccountId(),
                event.getAccountRoleId(),
                event.getEmail(),
                NotificationEventType.ORDER_FAILED_INVENTORY,
                variables,
                event.getChannels());

        failedEventSaverService.markSuccess(failedEventId);
        processedEventService.markProcessed(event.getId());

        log.info(
                "[Notification][Retry][InventoryReservationFailedEvent] Completed notification for orderId={}",
                event.getOrderId());
    }
}
