package com.teamsolution.notification.service;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationDispatcher {
  void send(
      UUID accountId,
      UUID accountRoleId,
      String email,
      String eventType,
      Map<String, String> variables,
      List<NotificationChannel> channels);

  void sendWeb(UUID accountId, UUID accountRoleId, String eventType, Map<String, String> variables);

  void sendEmail(
      UUID accountId,
      UUID accountRoleId,
      String email,
      String eventType,
      Map<String, String> variables);
}
