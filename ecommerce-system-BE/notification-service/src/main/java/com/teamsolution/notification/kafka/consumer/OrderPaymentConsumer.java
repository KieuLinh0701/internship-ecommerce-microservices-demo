package com.teamsolution.notification.kafka.consumer;

import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.notification.kafka.handler.OrderPaymentEventHandler;
import com.teamsolution.notification.service.ProcessedEventService;
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
public class OrderPaymentConsumer {

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final OrderPaymentEventHandler orderPaymentEventHandler;

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.ORDER_PAYMENT,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(ConsumerRecord<String, PaymentEvent> record) {

        KafkaTracingUtils.run(openTelemetry, record, () -> {
            PaymentEvent event = record.value();

            if (processedEventService.isDuplicate(event.getId())) {
                log.info(
                        "[Notification][PaymentEvent] Duplicate event, skipping id={}", event.getId());
                return;
            }

            orderPaymentEventHandler.handle(event);
        });
    }
}
