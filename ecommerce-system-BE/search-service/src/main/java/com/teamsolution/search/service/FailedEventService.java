package com.teamsolution.search.service;

import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FailedEventService {

  Map<FailedEventStatus, Long> getStats();

  Page<FailedEventResponse> getAll(Pageable pageable, FilterFailedEventRequest filter);

  FailedEventResponse retry(UUID id, UUID adminAccountId);

  FailedEventResponse ignore(UUID id, UUID adminAccountId);
}
