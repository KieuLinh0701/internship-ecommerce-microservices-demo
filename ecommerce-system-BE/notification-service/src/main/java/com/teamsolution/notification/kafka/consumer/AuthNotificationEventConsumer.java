package com.teamsolution.notification.kafka.consumer;

import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthNotificationEventConsumer {

    private final NotificationDispatcher notificationDispatcher;
    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;

    @KafkaListener(
            topics = KafkaTopics.AUTH_NOTIFICATION,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsumerRecord<String, AuthNotificationEvent> record) {
        record.headers().forEach(h ->
                System.out.println("header: " + h.key() + " = " + new String(h.value(), StandardCharsets.UTF_8)));

        Context ctx = openTelemetry.getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), record.headers(), KafkaTracingUtils.KAFKA_GETTER);

        try (Scope scope = ctx.makeCurrent()) {
            Span currentSpan = Span.current();

            if (currentSpan.getSpanContext().isValid()) {
                MDC.put("traceId", currentSpan.getSpanContext().getTraceId());
                MDC.put("spanId", currentSpan.getSpanContext().getSpanId());
            }

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

            log.info(
                    "[Notification][AuthNotificationEvent] Processing notification for accountId={}",
                    event.getAccountId());

            notificationDispatcher.send(
                    event.getAccountId(),
                    event.getAccountRoleId(),
                    event.getEmail(),
                    event.getType(),
                    event.getVariables(),
                    event.getChannels());

            processedEventService.markProcessed(event.getId());

            log.info(
                    "[Notification][AuthNotificationEvent] Completed notification for accountId={}",
                    event.getAccountId());
        } finally {
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
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
