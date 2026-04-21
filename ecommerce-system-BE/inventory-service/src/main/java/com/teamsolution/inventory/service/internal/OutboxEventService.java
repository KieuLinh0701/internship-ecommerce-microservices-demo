package com.teamsolution.inventory.service.internal;

import com.teamsolution.inventory.entity.FailedEvent;

import java.util.UUID;

public interface OutboxEventService {

  void markSent(UUID id);

  void handleFailure(UUID id, Throwable cause);

    void createFromFailedEvent(FailedEvent failedEvent);
}
