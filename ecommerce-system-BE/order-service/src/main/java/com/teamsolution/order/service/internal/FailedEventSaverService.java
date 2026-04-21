package com.teamsolution.order.service.internal;

import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface FailedEventSaverService {

  void saveFailedEvent(ConsumerRecord<?, ?> record, Exception ex);

  void markSuccess(UUID id);
}
