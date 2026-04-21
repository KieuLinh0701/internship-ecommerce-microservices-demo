package com.teamsolution.cart.service.internal;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.UUID;

public interface FailedEventSaverService {

  void saveFailedEvent(ConsumerRecord<?, ?> record, Exception ex);

  void markSuccess(UUID id);
}
