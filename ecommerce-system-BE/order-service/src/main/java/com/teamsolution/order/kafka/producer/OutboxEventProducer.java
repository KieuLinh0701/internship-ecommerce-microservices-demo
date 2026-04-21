package com.teamsolution.order.kafka.producer;

import com.teamsolution.order.entity.OutboxEvent;

public interface OutboxEventProducer {
  void publishEvent(OutboxEvent event);
}
