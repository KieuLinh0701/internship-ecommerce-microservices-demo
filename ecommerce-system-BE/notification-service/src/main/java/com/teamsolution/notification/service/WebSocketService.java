package com.teamsolution.notification.service;

import com.teamsolution.notification.dto.response.NotificationResponse;
import java.util.UUID;

public interface WebSocketService {
  void send(UUID accountId, UUID accountRoleId, NotificationResponse response);
}
