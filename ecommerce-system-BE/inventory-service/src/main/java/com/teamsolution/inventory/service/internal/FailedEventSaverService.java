package com.teamsolution.inventory.service.internal;

import com.teamsolution.inventory.entity.FailedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.UUID;

public interface FailedEventSaverService {

    void saveFailedEvent(ConsumerRecord<?, ?> record, Exception ex);

    FailedEvent saveExhaustedEvent(ConsumerRecord<?, ?> record, Exception ex);

    void markSuccess(UUID id);
}
