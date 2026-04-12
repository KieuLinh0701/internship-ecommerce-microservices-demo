package com.teamsolution.notification.kafka.handler;

import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.notification.service.FailedEventSaverService;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthNotificationEventHandler {

  private final NotificationDispatcher notificationDispatcher;
  private final ProcessedEventService processedEventService;
  private final FailedEventSaverService failedEventSaverService;

  @Transactional
  public void handleRetry(AuthNotificationEvent event, UUID failedEventId) {
    log.info(
        "[Notification][Retry][AuthNotificationEvent] Received event id={}, accountId={}",
        event.getId(),
        event.getAccountId());

    notificationDispatcher.send(
        event.getAccountId(),
        event.getAccountRoleId(),
        event.getEmail(),
        event.getType(),
        event.getVariables(),
        event.getChannels());

    failedEventSaverService.markSuccess(failedEventId);
    processedEventService.markProcessed(event.getId());

    log.info(
        "[Notification][Retry][AuthNotificationEvent] Completed notification for accountId={}",
        event.getAccountId());
  }
}
