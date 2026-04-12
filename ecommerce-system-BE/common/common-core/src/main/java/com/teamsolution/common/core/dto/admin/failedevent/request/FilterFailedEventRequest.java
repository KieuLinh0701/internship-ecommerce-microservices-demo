package com.teamsolution.common.core.dto.admin.failedevent.request;

import com.teamsolution.common.core.dto.common.request.BaseFilterRequest;
import com.teamsolution.common.core.enums.failedEvent.FailedEventErrorType;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterFailedEventRequest extends BaseFilterRequest {
  private FailedEventErrorType errorType;
  private FailedEventStatus status;
}
