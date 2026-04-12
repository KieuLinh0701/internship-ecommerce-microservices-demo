package com.teamsolution.notification.service.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.notification.dto.response.NotificationResponse;
import com.teamsolution.notification.entity.Notification;
import com.teamsolution.notification.enums.NotificationStatus;
import com.teamsolution.notification.mapper.NotificationMapper;
import com.teamsolution.notification.service.EmailService;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.NotificationService;
import com.teamsolution.notification.service.WebSocketService;
import com.teamsolution.notification.template.NotificationTemplateResolver;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcherImpl implements NotificationDispatcher {
  private final NotificationService notificationService;
  private final NotificationTemplateResolver templateResolver;
  private final NotificationMapper notificationMapper;
  private final WebSocketService webSocketService;
  private final EmailService emailService;

  @Override
  public void send(
      UUID accountId,
      UUID accountRoleId,
      String email,
      String eventType,
      Map<String, String> variables,
      List<NotificationChannel> channels) {

    for (NotificationChannel channel : channels) {
      switch (channel) {
        case WEB -> sendWeb(accountId, accountRoleId, eventType, variables);
        case EMAIL -> {
          if (email == null || email.isBlank()) {
            log.warn(
                "[Notification] Email is null, skipping EMAIL channel for event: {}", eventType);
            break;
          }
          sendEmail(accountId, accountRoleId, email, eventType, variables);
        }
        default -> log.warn("Unsupported channel: {}", channel);
      }
    }
  }

  @Override
  public void sendWeb(
      UUID accountId, UUID accountRoleId, String eventType, Map<String, String> variables) {
    String title = templateResolver.resolveWebTitle(eventType);
    String body = templateResolver.resolveWebMessage(eventType, variables);

    Notification entity =
        notificationService.save(
            accountId, accountRoleId, eventType, NotificationChannel.WEB, title, body);

    try {
      NotificationResponse dto = notificationMapper.toDto(entity);
      webSocketService.send(accountId, accountRoleId, dto);
      notificationService.updateStatus(entity.getId(), NotificationStatus.SENT);
    } catch (Exception e) {
      log.error("Failed to send web notification: {}", entity.getId(), e);
      notificationService.updateStatus(entity.getId(), NotificationStatus.FAILED);
    }
  }

  @Override
  public void sendEmail(
      UUID accountId,
      UUID accountRoleId,
      String email,
      String eventType,
      Map<String, String> variables) {
    String subject = templateResolver.resolveEmailSubject(eventType);
    String body = templateResolver.resolveEmailBody(eventType, variables);

    Notification entity =
        notificationService.save(
            accountId, accountRoleId, eventType, NotificationChannel.EMAIL, subject, body);

    try {
      emailService.send(email, subject, body);
      notificationService.updateStatus(entity.getId(), NotificationStatus.SENT);
    } catch (Exception e) {
      log.error("Failed to send email notification: {}", entity.getId(), e);
      notificationService.updateStatus(entity.getId(), NotificationStatus.FAILED);
    }
  }
}
