package com.teamsolution.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
  private UUID id;
  private String type;
  private String title;
  private String body;

  @JsonProperty("isRead")
  private boolean isRead;

  private LocalDateTime sentAt;
  private LocalDateTime readAt;
}
