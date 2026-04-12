package com.teamsolution.notification.specification;

import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.enums.failedEvent.FailedEventErrorType;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.notification.entity.FailedEvent;
import org.springframework.data.jpa.domain.Specification;

public class FailedEventSpecification {

  public static Specification<FailedEvent> build(FilterFailedEventRequest request) {

    Specification<FailedEvent> spec = Specification.where(null);

    if (request.getStatus() != null) {
      spec = spec.and(hasStatus(request.getStatus()));
    }

    if (request.getErrorType() != null) {
      spec = spec.and(hasErrorType(request.getErrorType()));
    }

    return spec;
  }

  public static Specification<FailedEvent> hasStatus(FailedEventStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<FailedEvent> hasErrorType(FailedEventErrorType errorType) {
    return (root, query, cb) -> cb.equal(root.get("errorType"), errorType);
  }
}
