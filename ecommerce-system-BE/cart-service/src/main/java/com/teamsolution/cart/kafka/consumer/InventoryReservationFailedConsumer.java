package com.teamsolution.cart.kafka.consumer;

import com.teamsolution.cart.kafka.handler.InventoryReservationFailedEventHandler;
import com.teamsolution.cart.service.internal.ProcessedEventService;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationFailedConsumer {

    private final InventoryReservationFailedEventHandler inventoryReservationFailedEventHandler;
    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;

    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RESERVATION_FAILED,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsumerRecord<String, InventoryReservationFailedEvent> record) {

        KafkaTracingUtils.run(openTelemetry, record, () -> {

            InventoryReservationFailedEvent event = record.value();

            log.info("Received InventoryReservationFailedEvent eventId={}, orderId={}",
                    event.getId(), event.getOrderId());

            if (processedEventService.isDuplicate(event.getId())) {
                return;
            }

            inventoryReservationFailedEventHandler.handle(event);
        });
    }
}
