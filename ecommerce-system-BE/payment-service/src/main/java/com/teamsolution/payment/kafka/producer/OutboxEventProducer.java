package com.teamsolution.payment.kafka.producer;

import com.teamsolution.payment.entity.OutboxEvent;

public interface OutboxEventProducer {
  void publishEvent(OutboxEvent event);
}
