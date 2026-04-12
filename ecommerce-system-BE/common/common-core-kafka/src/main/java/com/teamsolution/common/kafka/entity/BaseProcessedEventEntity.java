package com.teamsolution.common.kafka.entity;

import jakarta.persistence.Column;
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

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BaseProcessedEventEntity {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  private UUID eventId;

  @CreationTimestamp
  @Column(name = "processed_at", nullable = false)
  @Builder.Default
  private LocalDateTime processedAt = LocalDateTime.now();
}
