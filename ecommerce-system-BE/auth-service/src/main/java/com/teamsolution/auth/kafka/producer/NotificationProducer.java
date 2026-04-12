package com.teamsolution.auth.kafka.producer;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.enums.AuthEventType;
import com.teamsolution.auth.enums.EntityName;
import com.teamsolution.common.core.enums.notification.NotificationChannel;
import java.util.List;
import java.util.Map;

public interface NotificationProducer {
  void send(
      Account account,
      String type,
      List<NotificationChannel> channels,
      Map<String, String> variablesMap,
      AuthEventType eventType,
      EntityName aggregateType);

  void send(
      String email,
      String type,
      List<NotificationChannel> channels,
      Map<String, String> variablesMap,
      AuthEventType eventType,
      EntityName aggregateType);
}
