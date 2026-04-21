package com.teamsolution.order.service.internal;

import java.util.UUID;

public interface OutboxEventService {

  void markSent(UUID id);

  void handleFailure(UUID id, Throwable cause);
}
