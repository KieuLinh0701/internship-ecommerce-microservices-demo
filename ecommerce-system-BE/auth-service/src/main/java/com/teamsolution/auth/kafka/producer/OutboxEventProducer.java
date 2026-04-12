package com.teamsolution.auth.kafka.producer;

import com.teamsolution.auth.entity.OutboxEvent;

public interface OutboxEventProducer {
  void publishEvent(OutboxEvent event);
}
