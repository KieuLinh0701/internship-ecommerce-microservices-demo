package com.teamsolution.payment.service;

import java.util.UUID;

public interface ProcessedEventService {

  boolean isDuplicate(UUID eventId);

  void markProcessed(UUID eventId);
}
