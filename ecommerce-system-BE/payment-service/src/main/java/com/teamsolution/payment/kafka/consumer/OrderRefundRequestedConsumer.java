package com.teamsolution.payment.kafka.consumer;

import com.teamsolution.common.kafka.event.order.OrderRefundRequestedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.payment.kafka.handler.OrderRefundRequestedEventHandler;
import com.teamsolution.payment.service.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRefundRequestedConsumer {

    private static final String EVENT = "OrderRefundRequestedEvent";

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final OrderRefundRequestedEventHandler orderRefundRequestedEventHandler;

    @KafkaListener(
            topics = KafkaTopics.ORDER_REFUND_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleEvent(ConsumerRecord<String, OrderRefundRequestedEvent> record) {
        KafkaTracingUtils.run(openTelemetry, record, () -> {

            OrderRefundRequestedEvent event = record.value();
            log.info("[Payment][{}] Received event id={}, orderId={}, orderNumber={}, accountId={}, accountRoleId={}",
                    EVENT, event.getId(), event.getOrderId(), event.getOrderNumber(),
                    event.getAccountId(), event.getAccountRoleId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info("[Payment][{}] Duplicate event, skipping id={}", EVENT, event.getId());
                return;
            }

            orderRefundRequestedEventHandler.handle(event);
        });
    }
}