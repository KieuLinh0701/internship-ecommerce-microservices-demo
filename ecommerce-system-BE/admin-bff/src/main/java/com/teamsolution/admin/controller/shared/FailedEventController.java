package com.teamsolution.admin.controller.shared;

import com.teamsolution.admin.service.failedevent.FailedEventService;
import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/failed-events")
@RequiredArgsConstructor
public class FailedEventController {

  private final FailedEventService failedEventService;

  @GetMapping("/stats")
  public ResponseEntity<ApiResponse<Map<String, Map<FailedEventStatus, Long>>>> stats() {
    return ResponseEntity.ok(ApiResponse.success(failedEventService.fetchStats()));
  }

  @GetMapping("/{serviceId}")
  public ResponseEntity<ApiResponse<PageResponse<FailedEventResponse>>> listByService(
      @PathVariable String serviceId, @ModelAttribute FilterFailedEventRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(failedEventService.fetchByService(serviceId, request)));
  }

  @PostMapping("/{serviceId}/{id}/retry")
  public ResponseEntity<ApiResponse<FailedEventResponse>> retry(
      @PathVariable UUID id, @PathVariable String serviceId) {
    FailedEventResponse failedEventResponse = failedEventService.retry(serviceId, id);
    return ResponseEntity.ok(ApiResponse.success(failedEventResponse));
  }

  @PostMapping("/{serviceId}/{id}/ignore")
  public ResponseEntity<ApiResponse<FailedEventResponse>> ignore(
      @PathVariable UUID id, @PathVariable String serviceId) {
    FailedEventResponse failedEventResponse = failedEventService.ignore(serviceId, id);
    return ResponseEntity.ok(ApiResponse.success(failedEventResponse));
  }
}
