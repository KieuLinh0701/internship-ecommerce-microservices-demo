package com.teamsolution.admin.client.failedevent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.admin.client.BaseInternalClient;
import com.teamsolution.admin.dto.response.ServiceResponse;
import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import java.util.Map;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class FailedEventInternalClient extends BaseInternalClient {

  private static final ParameterizedTypeReference<ApiResponse<PageResponse<FailedEventResponse>>>
      FAILED_EVENT_PAGE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<ApiResponse<FailedEventResponse>>
      FAILED_EVENT_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<ApiResponse<Map<FailedEventStatus, Long>>>
      STATS_TYPE = new ParameterizedTypeReference<>() {};

  public FailedEventInternalClient(RestClient restClient, ObjectMapper objectMapper) {
    super(restClient, objectMapper);
  }

  public Map<FailedEventStatus, Long> fetchStats(String serviceId) {
    return restClient
        .get()
        .uri(buildUri(serviceId, "/internal/failed-events/stats", null))
        .retrieve()
        .body(STATS_TYPE)
        .getData();
  }

  public ServiceResponse fetchFailedEvents(String serviceId, FilterFailedEventRequest request) {
    try {
      PageResponse<FailedEventResponse> data =
          fetchPage(serviceId, "/internal/failed-events", request, FAILED_EVENT_PAGE);
      return ServiceResponse.ok(serviceId, data);
    } catch (RestClientResponseException ex) {
      return ServiceResponse.error(
          serviceId, extractMessage(ex.getResponseBodyAsString(), ex.getMessage()));
    } catch (Exception ex) {
      return ServiceResponse.error(serviceId, ex.getMessage());
    }
  }

  public FailedEventResponse retry(String serviceId, UUID eventId) {
    return postAction(
        serviceId, "/internal/failed-events/" + eventId + "/retry", null, FAILED_EVENT_TYPE);
  }

  public FailedEventResponse ignore(String serviceId, UUID eventId) {
    return postAction(
        serviceId, "/internal/failed-events/" + eventId + "/ignore", null, FAILED_EVENT_TYPE);
  }
}
