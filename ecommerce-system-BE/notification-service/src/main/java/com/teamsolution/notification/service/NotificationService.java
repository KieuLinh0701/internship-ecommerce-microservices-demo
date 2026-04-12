package com.teamsolution.notification.service;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.notification.dto.request.NotificationFilterRequest;
import com.teamsolution.notification.dto.response.NotificationResponse;
import com.teamsolution.notification.entity.Notification;
import com.teamsolution.notification.enums.NotificationStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

  Page<NotificationResponse> getNotifications(
      UUID accountId, UUID accountRoleId, NotificationFilterRequest filter, Pageable pageable);

  NotificationResponse getDetail(UUID id, UUID accountId, UUID accountRoleId);

  void delete(UUID id, UUID accountId, UUID accountRoleId);

  void markAsRead(UUID id, UUID accountId, UUID accountRoleId);

  void markAllAsRead(UUID accountId, UUID accountRoleId);

  Notification save(
      UUID accountId,
      UUID accountRoleId,
      String type,
      NotificationChannel channel,
      String title,
      String body);

  void updateStatus(UUID id, NotificationStatus status);
}
