package com.teamsolution.cart.service.internal;

import java.util.UUID;

public interface ProcessedEventService {

  boolean isDuplicate(UUID eventId);

  void markProcessed(UUID eventId);
}
