package com.teamsolution.inventory.controller.internal;

import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.common.jpa.utils.PageableUtils;
import com.teamsolution.inventory.service.customer.FailedEventService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/failed-events")
@RequiredArgsConstructor
public class FailedEventInternalController {

  private final FailedEventService failedEventService;

  @GetMapping("/stats")
  public ResponseEntity<ApiResponse<Map<FailedEventStatus, Long>>> getStats() {

    Map<FailedEventStatus, Long> stats = failedEventService.getStats();
    return ResponseEntity.ok(ApiResponse.success(stats));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<FailedEventResponse>>> getAll(
      @ModelAttribute FilterFailedEventRequest filterRequest) {

    Pageable pageable =
        PageableUtils.toPageable(
            filterRequest.getPage(),
            filterRequest.getSize(),
            "failedAt",
            filterRequest.getDirection(),
            false);

    Page<FailedEventResponse> failedEventDtos = failedEventService.getAll(pageable, filterRequest);
    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(failedEventDtos)));
  }

  @PostMapping("/{id}/retry")
  public ResponseEntity<ApiResponse<FailedEventResponse>> retry(@PathVariable UUID id) {

    UUID adminAccountId = SecurityUtils.getCurrentAccountId();
    FailedEventResponse failedEventResponse = failedEventService.retry(id, adminAccountId);

    return ResponseEntity.ok(ApiResponse.success(failedEventResponse));
  }

  @PostMapping("/{id}/ignore")
  public ResponseEntity<ApiResponse<FailedEventResponse>> ignore(@PathVariable UUID id) {

    UUID adminAccountId = SecurityUtils.getCurrentAccountId();
    FailedEventResponse failedEventResponse = failedEventService.ignore(id, adminAccountId);

    return ResponseEntity.ok(ApiResponse.success(failedEventResponse));
  }
}
