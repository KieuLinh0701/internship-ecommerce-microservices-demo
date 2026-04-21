package com.teamsolution.inventory.kafka.consumer;

import com.teamsolution.common.kafka.event.order.OrderPaymentTimeoutEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.inventory.kafka.handler.OrderPaymentTimeOutEventHandler;
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
public class OrderPaymentTimeOutConsumer {

    private final OrderPaymentTimeOutEventHandler orderPaymentTimeOutEventHandler;
    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final static String EVENT = "OrderPaymentTimeOutEvent";

    @KafkaListener(
            topics = KafkaTopics.ORDER_CANCELLED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handle(ConsumerRecord<String, OrderPaymentTimeoutEvent> record) {

        KafkaTracingUtils.run(openTelemetry, record, () -> {
            OrderPaymentTimeoutEvent event = record.value();

            log.info(
                    "[{}] Received OrderCancelledEvent eventId={}, orderId={}",
                    EVENT,
                    event.getId(),
                    event.getOrderId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info("[Inventory][{}] Duplicate event, skipping id={}", EVENT, event.getId());
                return;
            }

            orderPaymentTimeOutEventHandler.handle(event);
        });
    }
}
