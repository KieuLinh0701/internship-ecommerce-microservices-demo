package com.teamsolution.payment.service;

import com.teamsolution.payment.entity.FailedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.UUID;

public interface FailedEventSaverService {

  void saveFailedEvent(ConsumerRecord<?, ?> record, Exception ex);

  void markSuccess(UUID id);

    FailedEvent saveExhaustedEvent(ConsumerRecord<?, ?> record, Exception ex);
}
