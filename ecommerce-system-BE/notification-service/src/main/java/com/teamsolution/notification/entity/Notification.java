// chưa có id liên quan ẻ có thể xem kiểu đơn hàng trong thông báo ý
package com.teamsolution.notification.entity;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.notification.enums.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @Column(name = "account_id")
  private UUID accountId;

  @Column(name = "account_role_id")
  private UUID accountRoleId;

  @Column(name = "type")
  private String type;

  @Enumerated(EnumType.STRING)
  @Column(name = "channel")
  private NotificationChannel channel;

  @Column(name = "title", columnDefinition = "varchar(255)")
  private String title;

  @Column(name = "body", columnDefinition = "text")
  private String body;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private NotificationStatus status = NotificationStatus.PENDING;

  @Column(name = "retry_count")
  private int retryCount;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = false;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public boolean isRead() {
    return readAt != null;
  }
}
