package com.teamsolution.notification.service;

import java.util.UUID;

public interface ProcessedEventService {

  boolean isDuplicate(UUID eventId);

  void markProcessed(UUID eventId);
}
