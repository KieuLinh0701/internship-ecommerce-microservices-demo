package com.teamsolution.common.kafka.entity;

import com.teamsolution.common.core.enums.failedEvent.FailedEventErrorType;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.core.util.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BaseFailedEventEntity {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @Column(nullable = false)
  private String topic;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "aggregate_id")
  private UUID aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(columnDefinition = "jsonb", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private String payload;

  @Column(name = "payload_class")
  private String payloadClass;

  @Enumerated(EnumType.STRING)
  @Column(name = "error_type", nullable = false)
  private FailedEventErrorType errorType;

  @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "retry_count", nullable = false)
  private int retryCount = 0;

  @Column(name = "max_retry", nullable = false)
  @Builder.Default
  private int maxRetry = 3;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FailedEventStatus status = FailedEventStatus.FAILED;

  @Column(name = "failed_at", nullable = false)
  @Builder.Default
  private LocalDateTime failedAt = LocalDateTime.now();

  @Column(name = "last_retry_at")
  private LocalDateTime lastRetryAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;

  @Column(name = "resolved_by")
  private UUID resolvedBy;

  @Column(name = "last_retried_by")
  private UUID lastRetriedBy;
}
