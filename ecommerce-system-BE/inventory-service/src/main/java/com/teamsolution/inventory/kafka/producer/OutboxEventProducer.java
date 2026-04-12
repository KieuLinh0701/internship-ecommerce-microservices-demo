package com.teamsolution.inventory.kafka.producer;

import com.teamsolution.inventory.entity.OutboxEvent;

public interface OutboxEventProducer {
  void publishEvent(OutboxEvent event);
}
