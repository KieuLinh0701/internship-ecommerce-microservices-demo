package com.teamsolution.admin.service.failedevent;

import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import java.util.Map;
import java.util.UUID;

public interface FailedEventService {
  Map<String, Map<FailedEventStatus, Long>> fetchStats();

  PageResponse<FailedEventResponse> fetchByService(
      String serviceId, FilterFailedEventRequest request);

  FailedEventResponse retry(String serviceId, UUID eventId);

  FailedEventResponse ignore(String serviceId, UUID eventId);
}
