package com.teamsolution.inventory.kafka.consumer;

import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.inventory.kafka.handler.OrderCreatedEventHandler;
import com.teamsolution.inventory.service.internal.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {
    private static final String EVENT = "OrderCreatedEvent";

    private final OrderCreatedEventHandler orderCreatedEventHandler;
    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handle(ConsumerRecord<String, OrderCreatedEvent> record) {
        KafkaTracingUtils.run(openTelemetry, record, () -> {
            OrderCreatedEvent event = record.value();
            log.info(
                    "[{}] Received event id={}, orderId={}",
                    EVENT,
                    event.getId(),
                    event.getOrderId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info(
                        "[{}] Duplicate event, skipping id={}",
                        EVENT,
                        event.getId());
                return;
            }

            orderCreatedEventHandler.handle(event);
        });
    }

    // Test Retry by throwing TemporaryException
    //    @KafkaListener(
    //            topics = KafkaTopics.AUTH_NOTIFICATION,
    //            groupId = "${spring.kafka.consumer.group-id}")
    //    public void handle(
    //            AuthNotificationEvent event,
    //            @Header(name = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer attempt
    //    ) {
    //        log.info("Attempt: {}", attempt);
    //
    //        throw new TemporaryException(ErrorCode.NOTIFICATION_NOT_FOUND);
    //    }
}
