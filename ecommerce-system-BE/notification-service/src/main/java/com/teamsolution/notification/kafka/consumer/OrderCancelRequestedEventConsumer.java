package com.teamsolution.notification.kafka.consumer;

import com.teamsolution.common.kafka.event.order.OrderCancelRequestedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.notification.grpc.client.AuthGrpcClient;
import com.teamsolution.notification.kafka.handler.OrderCancelRequestedEventHandler;
import com.teamsolution.notification.service.NotificationDispatcher;
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
public class OrderCancelRequestedEventConsumer {

    private final ProcessedEventService processedEventService;
    private final OpenTelemetry openTelemetry;
    private final NotificationDispatcher notificationDispatcher;
    private final AuthGrpcClient authGrpcClient;
    private final OrderCancelRequestedEventHandler orderCancelRequestedEventHandler;


    @Transactional
    @KafkaListener(
            topics = KafkaTopics.ORDER_CANCEL_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(ConsumerRecord<String, OrderCancelRequestedEvent> record) {

        KafkaTracingUtils.run(openTelemetry, record, () -> {

            OrderCancelRequestedEvent event = record.value();
            log.info("[Notification] Received OrderCancelRequestedEvent event: orderId={}, orderNumber={}",
                    event.getOrderId(), event.getOrderNumber());

            if (processedEventService.isDuplicate(event.getId())) {
                log.info(
                        "[Notification][OrderCancelRequestedEvent] Duplicate event, skipping id={}",
                        event.getId());
                return;
            }

            orderCancelRequestedEventHandler.handle(event);
        });
    }
}
