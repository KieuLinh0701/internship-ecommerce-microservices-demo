package com.teamsolution.payment.service;

import com.teamsolution.payment.entity.FailedEvent;

import java.util.UUID;

public interface OutboxEventService {

    void markSent(UUID id);

    void handleFailure(UUID id, Throwable cause);

    void createFromFailedEvent(FailedEvent failedEvent);
}
