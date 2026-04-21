package com.teamsolution.notification.kafka.consumer;

import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.notification.kafka.handler.AuthNotificationEventHandler;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
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
public class AuthNotificationEventConsumer {

    private final NotificationDispatcher notificationDispatcher;
    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final AuthNotificationEventHandler authNotificationEventHandler;

    @KafkaListener(
            topics = KafkaTopics.AUTH_NOTIFICATION,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsumerRecord<String, AuthNotificationEvent> record) {
        KafkaTracingUtils.run(openTelemetry, record, () -> {

            AuthNotificationEvent event = record.value();
            log.info(
                    "[Notification][AuthNotificationEvent] Received event id={}, accountId={}",
                    event.getId(),
                    event.getAccountId());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info(
                        "[Notification][AuthNotificationEvent] Duplicate event, skipping id={}", event.getId());
                return;
            }

            authNotificationEventHandler.handle(event);
        });
    }

    // Test Retry by throwing TemporaryException
    //    @KafkaListener(
    //            topics = KafkaTopics.AUTH_NOTIFICATION,
    //            groupId = "${spring.kafka.consumer.group-id}")
    //    public void handle(
    //            AuthNotificationEvent event,
    //            @Header(name = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer attempt
    //    ) {
    //        log.info("Attempt: {}", attempt);
    //
    //        throw new TemporaryException(ErrorCode.NOTIFICATION_NOT_FOUND);
    //    }
}
