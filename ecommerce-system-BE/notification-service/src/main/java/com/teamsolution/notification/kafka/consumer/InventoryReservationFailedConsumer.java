package com.teamsolution.notification.kafka.consumer;

import com.teamsolution.common.kafka.constant.NotificationEventType;
import com.teamsolution.common.kafka.constant.NotificationVariables;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationFailedConsumer {

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final NotificationDispatcher notificationDispatcher;

    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RESERVATION_FAILED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(ConsumerRecord<String, InventoryReservationFailedEvent> record) {
        KafkaTracingUtils.run(openTelemetry, record, () -> {

            InventoryReservationFailedEvent event = record.value();

            log.info(
                    "[Order] Received OrderCreatedEvent for orderId={}, orderNumber={}, accountId={}, accountRoleId={}",
                    event.getOrderId(),
                    event.getOrderNumber(),
                    event.getAccountId(),
                    event.getAccountRoleId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info(
                        "[Notification][AuthNotificationEvent] Duplicate event, skipping id={}", event.getId());
                return;
            }

            log.info("[Order] Handling inventory failure for orderId={}", event.getOrderId());

            Map<String, String> variables = Map.of(
                    NotificationVariables.ORDER_NUMBER, event.getOrderNumber());
            String eventType = NotificationEventType.ORDER_FAILED_INVENTORY;

            notificationDispatcher.send(
                    event.getAccountId(),
                    event.getAccountRoleId(),
                    event.getEmail(),
                    eventType,
                    variables,
                    event.getChannels()
            );

            log.info("[Order] Completed handling inventory failure for orderId={}", event.getOrderId());
        });
    }
}
