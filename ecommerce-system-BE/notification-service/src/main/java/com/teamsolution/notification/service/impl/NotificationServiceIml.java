package com.teamsolution.notification.service.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.notification.dto.request.NotificationFilterRequest;
import com.teamsolution.notification.dto.response.NotificationResponse;
import com.teamsolution.notification.entity.Notification;
import com.teamsolution.notification.enums.NotificationStatus;
import com.teamsolution.notification.exception.ErrorCode;
import com.teamsolution.notification.mapper.NotificationMapper;
import com.teamsolution.notification.repository.NotificationRepository;
import com.teamsolution.notification.service.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceIml implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  public Page<NotificationResponse> getNotifications(
      UUID accountId, UUID accountRoleId, NotificationFilterRequest filter, Pageable pageable) {
    return notificationRepository
        .findActiveNotifications(
            accountId,
            accountRoleId,
            NotificationChannel.WEB,
            NotificationStatus.SENT,
            filter.getKeyword(),
            filter.getIsRead(),
            filter.getFromDate(),
            filter.getToDate(),
            pageable)
        .map(notificationMapper::toDto);
  }

  @Override
  public NotificationResponse getDetail(UUID id, UUID accountId, UUID accountRoleId) {
    Notification notification = findByIdAndAccountIdAndAccountRoleId(id, accountId, accountRoleId);
    return notificationMapper.toDto(notification);
  }

  @Override
  public void delete(UUID id, UUID accountId, UUID accountRoleId) {
    Notification notification = findByIdAndAccountIdAndAccountRoleId(id, accountId, accountRoleId);

    notification.setIsDeleted(true);
    notificationRepository.save(notification);
  }

  @Override
  public void markAsRead(UUID id, UUID accountId, UUID accountRoleId) {
    Notification notification = findByIdAndAccountIdAndAccountRoleId(id, accountId, accountRoleId);
    if (notification.getReadAt() != null) return;
    notification.setReadAt(LocalDateTime.now());
    notificationRepository.save(notification);
  }

  @Override
  public void markAllAsRead(UUID accountId, UUID accountRoleId) {
    notificationRepository.markAllAsRead(
        accountId,
        accountRoleId,
        NotificationChannel.WEB,
        NotificationStatus.SENT,
        LocalDateTime.now());
  }

  public Notification save(
      UUID accountId,
      UUID accountRoleId,
      String type,
      NotificationChannel channel,
      String title,
      String body) {
    Notification notification =
        Notification.builder()
            .accountId(accountId)
            .accountRoleId(accountRoleId)
            .type(type)
            .channel(channel)
            .title(title)
            .body(body)
            .build();

    return notificationRepository.save(notification);
  }

  @Override
  public void updateStatus(UUID id, NotificationStatus status) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

    notification.setStatus(status);

    if (status == NotificationStatus.SENT) {
      notification.setSentAt(LocalDateTime.now());
    } else if (status == NotificationStatus.FAILED) {
      notification.setRetryCount(notification.getRetryCount() + 1);
    }
    notificationRepository.save(notification);
  }

  private Notification findByIdAndAccountIdAndAccountRoleId(
      UUID id, UUID accountId, UUID accountRoleId) {
    return notificationRepository
        .findActiveNotification(id, accountId, accountRoleId, NotificationStatus.SENT)
        .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
  }
}
