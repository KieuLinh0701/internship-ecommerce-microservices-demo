package com.teamsolution.search.kafka.handler;

import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.search.service.FailedEventSaverService;
import com.teamsolution.search.service.ProcessedEventService;
import com.teamsolution.search.service.ProductSearchService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStatusChangeEventHandler {

  private final ProcessedEventService processedEventService;
  private final FailedEventSaverService failedEventSaverService;
  private final ProductSearchService productSearchService;

  @Transactional
  public void handleRetry(ProductStatusChangedEvent event, UUID failedEventId) {
    log.info("[Retry][ProductStatusChangedEvent] Processing productId={}", event.getProductId());

    productSearchService.updateStatus(event);

    failedEventSaverService.markSuccess(failedEventId);
    processedEventService.markProcessed(event.getId());

    log.info("[Retry][ProductStatusChangedEvent] Completed for productId={}", event.getProductId());
  }
}
