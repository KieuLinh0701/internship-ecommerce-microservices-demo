package com.teamsolution.search.kafka.consumer;

import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.search.kafka.handler.ProductStatusChangeEventHandler;
import com.teamsolution.search.service.ProcessedEventService;
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
public class ProductStatusChangeEventConsumer {

    private static final String EVENT = "ProductStatusChangedEvent";

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final ProductStatusChangeEventHandler productStatusChangeEventHandler;

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_STATUS_CHANGED,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsumerRecord<String, ProductStatusChangedEvent> record) {
        KafkaTracingUtils.run(openTelemetry, record, () -> {


            ProductStatusChangedEvent event = record.value();
            log.info("[Search][{}] Received event id={}, productId={}",
                    EVENT, event.getId(), event.getProductId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info("[Search][{}] Duplicate event, skipping id={}", EVENT, event.getId());
                return;
            }

            productStatusChangeEventHandler.handle(event);
        });
    }

    // Test Retry by throwing TemporaryException
    //    @KafkaListener(
    //            topics = KafkaTopics.PRODUCT_STATUS_CHANGED,
    //            groupId = "${spring.kafka.consumer.group-id}")
    //    public void handle(
    //            ProductStatusChangedEvent event,
    //            @Header(name = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer attempt
    //    ) {
    //        log.info("Attempt: {}", attempt);
    //
    //        throw new TemporaryException(ErrorCode.PRODUCT_NOT_FOUND);
    //    }
}
