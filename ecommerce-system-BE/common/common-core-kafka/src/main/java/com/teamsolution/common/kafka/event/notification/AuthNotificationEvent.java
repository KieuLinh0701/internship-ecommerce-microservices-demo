package com.teamsolution.common.kafka.event.notification;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.kafka.event.BaseEvent;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthNotificationEvent extends BaseEvent {
  private UUID accountRoleId;
  private UUID accountId;
  private String email;
  private String type;
  private List<NotificationChannel> channels;
  private Map<String, String> variables;
}
