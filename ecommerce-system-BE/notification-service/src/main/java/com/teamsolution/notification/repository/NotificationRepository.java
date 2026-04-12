package com.teamsolution.notification.repository;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.notification.entity.Notification;
import com.teamsolution.notification.enums.NotificationStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends BaseRepository<Notification, UUID> {

  @Query(
      value =
          """
            SELECT * FROM notifications n
            WHERE n.channel = :#{#channel.name()}
            AND n.status = :#{#status.name()}
            AND n.is_deleted = false
            AND (
                n.account_role_id = :accountRoleId
                OR (n.account_id = :accountId AND n.account_role_id IS NULL)
            )
            AND (CAST(:keyword AS text) IS NULL
                OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))
                OR LOWER(n.body) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
            AND (CAST(:isRead AS boolean) IS NULL
                OR (CAST(:isRead AS boolean) = true AND n.read_at IS NOT NULL)
                OR (CAST(:isRead AS boolean) = false AND n.read_at IS NULL))
            AND (CAST(:fromDate AS timestamp) IS NULL OR n.sent_at >= CAST(:fromDate AS timestamp))
            AND (CAST(:toDate AS timestamp) IS NULL OR n.sent_at <= CAST(:toDate AS timestamp))
            """,
      countQuery =
          """
                    SELECT COUNT(*) FROM notifications n
                    WHERE n.channel = :#{#channel.name()}
                    AND n.status = :#{#status.name()}
                    AND n.is_deleted = false
                    AND (
                        n.account_role_id = :accountRoleId
                        OR (n.account_id = :accountId AND n.account_role_id IS NULL)
                    )
                    AND (:keyword IS NULL
                        OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(n.body) LIKE LOWER(CONCAT('%', :keyword, '%')))
                    AND (:isRead IS NULL
                        OR (:isRead = true AND n.read_at IS NOT NULL)
                        OR (:isRead = false AND n.read_at IS NULL))
                    AND (CAST(:fromDate AS timestamp) IS NULL OR n.sent_at >= CAST(:fromDate AS timestamp))
                    AND (CAST(:toDate AS timestamp) IS NULL OR n.sent_at <= CAST(:toDate AS timestamp))
                    """,
      nativeQuery = true)
  Page<Notification> findActiveNotifications(
      @Param("accountId") UUID accountId,
      @Param("accountRoleId") UUID accountRoleId,
      @Param("channel") NotificationChannel channel,
      @Param("status") NotificationStatus status,
      @Param("keyword") String keyword,
      @Param("isRead") Boolean isRead,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Modifying
  @Transactional
  @Query(
      """
                UPDATE Notification n SET n.readAt = :now
                WHERE n.accountId = :accountId
                AND (n.accountRoleId = :accountRoleId OR n.accountRoleId IS NULL)
                AND n.channel = :channel
                AND n.status = :status
                AND n.isDeleted = false
                AND n.readAt IS NULL
            """)
  void markAllAsRead(
      @Param("accountId") UUID accountId,
      @Param("accountRoleId") UUID accountRoleId,
      @Param("channel") NotificationChannel channel,
      @Param("status") NotificationStatus status,
      @Param("now") LocalDateTime now);

  @Query(
      """
            SELECT n FROM Notification n
            WHERE n.id = :id
              AND n.accountId = :accountId
              AND (n.accountRoleId = :accountRoleId OR n.accountRoleId IS NULL)
              AND n.status = :status
              AND n.isDeleted = false
            """)
  Optional<Notification> findActiveNotification(
      @Param("id") UUID id,
      @Param("accountId") UUID accountId,
      @Param("accountRoleId") UUID accountRoleId,
      @Param("status") NotificationStatus status);
}
