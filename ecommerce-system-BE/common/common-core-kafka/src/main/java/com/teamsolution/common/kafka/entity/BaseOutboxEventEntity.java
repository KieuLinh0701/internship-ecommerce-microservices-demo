package com.teamsolution.common.kafka.entity;

import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.common.kafka.enums.OutboxEventStatus;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BaseOutboxEventEntity {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @Column(name = "trace_id")
  private String traceId;

  @Column(name = "aggregate_type", nullable = false)
  private String aggregateType;

  @Column(name = "aggregate_id", nullable = false)
  private UUID aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(columnDefinition = "jsonb", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private String payload;

  @Column(name = "error_message")
  private String errorMessage;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private OutboxEventStatus status = OutboxEventStatus.PENDING;

  @Column(name = "retry_count", nullable = false)
  private int retryCount = 0;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_retry_at")
  private LocalDateTime lastRetryAt;

  @Column(name = "next_retry_at")
  private LocalDateTime nextRetryAt;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;
}
