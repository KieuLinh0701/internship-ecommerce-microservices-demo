package com.teamsolution.search.kafka.handler;

import com.teamsolution.common.core.exception.TemporaryException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.search.service.FailedEventSaverService;
import com.teamsolution.search.service.ProcessedEventService;
import com.teamsolution.search.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductChangeEventHandler {

    private static final String EVENT = "ProductChangedEvent";

    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;
    private final ProductSearchService productSearchService;

    @Transactional
    public void handle(ProductChangedEvent event) {
        log.info("[Search][{}] Processing for productId={}", EVENT, event.getProductId());

        try {
            productSearchService.sync(event);
        } catch (Exception ex) {
            throw new TemporaryException(CommonErrorCode.DATABASE_ERROR, ex);
        }

        processedEventService.markProcessed(event.getId());

        log.info("[Search][{}] Completed for productId={}", EVENT, event.getProductId());
    }

    @Transactional
    public void handleRetry(ProductChangedEvent event, UUID failedEventId) {
        log.info("[Retry][Search][{}] Processing productId={}", EVENT, event.getProductId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info("[Retry][Search][{}] Completed for productId={}", EVENT, event.getProductId());
    }
}