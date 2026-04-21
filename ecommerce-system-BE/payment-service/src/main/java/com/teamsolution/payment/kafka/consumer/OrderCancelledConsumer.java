package com.teamsolution.payment.kafka.consumer;

import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.payment.kafka.handler.OrderCancelledEventHandler;
import com.teamsolution.payment.service.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleEvent(ConsumerRecord<String, OrderCancelledEvent> record) {
        KafkaTracingUtils.run(openTelemetry, record, () -> {

            OrderCancelledEvent event = record.value();
            log.info("[Payment][{}] Received event id={}, orderId={}, orderNumber={}, needRefund={}",
                    EVENT, event.getId(), event.getOrderId(), event.getOrderNumber(), event.isNeedRefund());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info("[Payment][{}] Duplicate event, skipping id={}", EVENT, event.getId());
                return;
            }

            orderCancelledEventHandler.handle(event);
        });
    }
}
