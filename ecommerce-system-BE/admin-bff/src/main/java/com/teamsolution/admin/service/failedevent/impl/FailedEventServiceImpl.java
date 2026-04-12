// Virtual thread executor for non-blocking parallel calls to consumer services.
package com.teamsolution.admin.service.failedevent.impl;

import com.teamsolution.admin.client.failedevent.FailedEventInternalClient;
import com.teamsolution.admin.config.properties.ConsumerServicesProperties;
import com.teamsolution.admin.exception.ErrorCode;
import com.teamsolution.admin.service.failedevent.FailedEventService;
import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.core.exception.AppException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedEventServiceImpl implements FailedEventService {

  private final ConsumerServicesProperties consumerServicesProperties;
  private final FailedEventInternalClient internalEventClient;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  @Override
  public Map<String, Map<FailedEventStatus, Long>> fetchStats() {
    SecurityContext callerContext = SecurityContextHolder.getContext();

    List<CompletableFuture<Map.Entry<String, Map<FailedEventStatus, Long>>>> futures =
        consumerServicesProperties.getFailedEventServices().stream()
            .map(
                serviceId ->
                    CompletableFuture.supplyAsync(
                            () -> {
                              SecurityContextHolder.setContext(callerContext);
                              try {
                                return Map.entry(
                                    serviceId, internalEventClient.fetchStats(serviceId));
                              } finally {
                                SecurityContextHolder.clearContext();
                              }
                            },
                            executor)
                        .orTimeout(consumerServicesProperties.getTimeout(), TimeUnit.SECONDS)
                        .exceptionally(ex -> Map.entry(serviceId, Map.of())))
            .toList();

    return futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public PageResponse<FailedEventResponse> fetchByService(
      String serviceId, FilterFailedEventRequest request) {
    checkServiceId(serviceId);
    return internalEventClient.fetchFailedEvents(serviceId, request).getPage();
  }

  @Override
  public FailedEventResponse retry(String serviceId, UUID eventId) {

    checkServiceId(serviceId);

    return internalEventClient.retry(serviceId, eventId);
  }

  @Override
  public FailedEventResponse ignore(String serviceId, UUID eventId) {
    checkServiceId(serviceId);

    return internalEventClient.ignore(serviceId, eventId);
  }

  private void checkServiceId(String serviceId) {
    if (!consumerServicesProperties.getFailedEventServices().contains(serviceId))
      throw new AppException(ErrorCode.UNKNOWN_SERVICE_ID, serviceId);
  }
}
