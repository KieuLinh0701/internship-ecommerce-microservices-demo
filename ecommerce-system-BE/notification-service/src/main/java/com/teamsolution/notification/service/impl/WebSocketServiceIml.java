package com.teamsolution.notification.service.impl;

import com.teamsolution.notification.dto.response.NotificationResponse;
import com.teamsolution.notification.service.WebSocketService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketServiceIml implements WebSocketService {

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void send(UUID accountId, UUID accountRoleId, NotificationResponse response) {
    String destination = resolveDestination(accountId, accountRoleId);
    messagingTemplate.convertAndSend(destination, response);
  }

  private String resolveDestination(UUID accountId, UUID accountRoleId) {
    if (accountRoleId != null) {
      return "/topic/notifications/role/" + accountRoleId;
    }
    return "/topic/notifications/account/" + accountId;
  }
}
