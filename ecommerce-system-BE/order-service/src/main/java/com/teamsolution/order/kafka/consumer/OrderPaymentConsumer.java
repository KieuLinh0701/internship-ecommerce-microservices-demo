package com.teamsolution.order.kafka.consumer;

import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.order.kafka.handler.OrderPaymentEventHandler;
import com.teamsolution.order.service.internal.ProcessedEventService;
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
public class OrderPaymentConsumer {

  private static final String EVENT = "PaymentEvent";

  private final ProcessedEventService processedEventService;
  private final OpenTelemetry openTelemetry;
  private final OrderPaymentEventHandler orderPaymentEventHandler;

  @KafkaListener(topics = KafkaTopics.ORDER_PAYMENT, groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  public void handle(ConsumerRecord<String, PaymentEvent> record) {
    KafkaTracingUtils.run(
        openTelemetry,
        record,
        () -> {
          PaymentEvent event = record.value();
          log.info(
              "[Order][{}] Received event id={}, orderId={}, status={}",
              EVENT,
              event.getId(),
              event.getOrderId(),
              event.getStatus());

          if (processedEventService.isDuplicate(event.getId())) {
            log.info("[Order][{}] Duplicate event, skipping id={}", EVENT, event.getId());
            return;
          }

          orderPaymentEventHandler.handle(event);
        });
  }
}
