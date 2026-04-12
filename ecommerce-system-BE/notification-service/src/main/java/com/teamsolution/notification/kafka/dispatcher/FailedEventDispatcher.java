package com.teamsolution.notification.kafka.dispatcher;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.notification.kafka.handler.AuthNotificationEventHandler;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

  private final AuthNotificationEventHandler authNotificationEventHandler;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void dispatch(String topic, Object event, UUID failedEventId) {
    switch (topic) {
      case KafkaTopics.AUTH_NOTIFICATION ->
          authNotificationEventHandler.handleRetry((AuthNotificationEvent) event, failedEventId);
      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    }
  }
}
