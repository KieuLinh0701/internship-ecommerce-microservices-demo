package com.teamsolution.inventory.service.customer;

import java.util.UUID;

public interface ProcessedEventService {

  boolean isDuplicate(UUID eventId);

  void markProcessed(UUID eventId);
}
