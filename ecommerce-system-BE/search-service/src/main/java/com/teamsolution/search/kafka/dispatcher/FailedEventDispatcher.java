package com.teamsolution.search.kafka.dispatcher;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.search.kafka.handler.ProductChangeEventHandler;
import com.teamsolution.search.kafka.handler.ProductStatusChangeEventHandler;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

  private final ProductChangeEventHandler productChangeEventHandler;
  private final ProductStatusChangeEventHandler productStatusChangeEventHandler;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void dispatch(String topic, Object event, UUID failedEventId) {
    switch (topic) {
      case KafkaTopics.PRODUCT_CHANGED ->
          productChangeEventHandler.handleRetry((ProductChangedEvent) event, failedEventId);
      case KafkaTopics.PRODUCT_STATUS_CHANGED ->
          productStatusChangeEventHandler.handleRetry(
              (ProductStatusChangedEvent) event, failedEventId);
      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    }
  }
}
