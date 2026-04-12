package com.teamsolution.notification.dto.request;

import com.teamsolution.common.core.dto.common.request.BaseFilterRequest;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationFilterRequest extends BaseFilterRequest {
  private String keyword;
  private Boolean isRead;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
}
