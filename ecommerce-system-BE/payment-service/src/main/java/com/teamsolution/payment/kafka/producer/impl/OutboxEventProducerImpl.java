package com.teamsolution.payment.kafka.producer.impl;

import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.payment.entity.OutboxEvent;
import com.teamsolution.payment.kafka.producer.OutboxEventProducer;
import com.teamsolution.payment.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OutboxEventProducerImpl implements OutboxEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void publishEvent(OutboxEvent event) {
        String topic = getTopicForEvent();
        Object payload =
                parsePayload(event.getPayload());

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic,
                null,
                String.valueOf(event.getAggregateId()),
                payload
        );

        KafkaTracingUtils.addTraceHeader(record, event.getTraceId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);

        future.whenComplete(
                (result, ex) -> {
                    if (ex != null) {
                        outboxEventService.handleFailure(event.getId(), ex);
                    } else {
                        outboxEventService.markSent(event.getId());
                    }
                });
    }

    private Object parsePayload(String payload) {
        return JsonUtils.fromJson(payload, PaymentEvent.class);
    }

  private String getTopicForEvent() {
      return KafkaTopics.ORDER_PAYMENT;
  }
}
