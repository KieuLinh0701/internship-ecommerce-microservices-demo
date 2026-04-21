package com.teamsolution.search.kafka.consumer;

import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.search.kafka.handler.ProductChangeEventHandler;
import com.teamsolution.search.service.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductChangeEventConsumer {

    private static final String EVENT = "ProductChangedEvent";

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final ProductChangeEventHandler productChangeEventHandler;

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_CHANGED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(ConsumerRecord<String, ProductChangedEvent> record) {

        KafkaTracingUtils.run(openTelemetry, record, () -> {

            ProductChangedEvent event = record.value();
            log.info("[Search][{}] Received event id={}, productId={}",
                    EVENT, event.getId(), event.getProductId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info("[Search][{}] Duplicate event, skipping id={}", EVENT, event.getId());
                return;
            }

            productChangeEventHandler.handle(event);
        });
    }
}