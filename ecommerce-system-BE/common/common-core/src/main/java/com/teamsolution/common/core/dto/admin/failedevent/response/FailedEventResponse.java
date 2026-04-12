package com.teamsolution.common.core.dto.admin.failedevent.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class FailedEventResponse {

  private UUID id;
  private String topic;
  private UUID eventId;
  private String eventType;
  private String errorMessage;
  private int retryCount;
  private int maxRetry;
  private String status;
  private LocalDateTime failedAt;
  private LocalDateTime lastRetryAt;
  private LocalDateTime nextRetryAt;
  private LocalDateTime resolvedAt;
}
