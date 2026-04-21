package com.teamsolution.inventory.kafka.consumer;

import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.inventory.kafka.handler.OrderCancelledEventHandler;
import com.teamsolution.inventory.service.internal.ProcessedEventService;
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
public class OrderCancelledConsumer {
    private static final String EVENT = "OrderCancelledEvent";

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final OrderCancelledEventHandler orderCancelledEventHandler;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CANCELLED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handle(ConsumerRecord<String, OrderCancelledEvent> record) {

        KafkaTracingUtils.run(openTelemetry, record, () -> {
            OrderCancelledEvent event = record.value();

            log.info("[Inventory][{}] Received event id={}, orderId={}, previousStatus={}",
                    EVENT, event.getId(), event.getOrderId(), event.getPreviousStatus());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info("[Inventory][{}] Duplicate event, skipping id={}", EVENT, event.getId());
                return;
            }

            orderCancelledEventHandler.handle(event);
        });
    }
}
