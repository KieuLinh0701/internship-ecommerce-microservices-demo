package com.teamsolution.payment.repository;

import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.payment.entity.FailedEvent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface FailedEventRepository
    extends BaseRepository<FailedEvent, UUID>, JpaSpecificationExecutor<FailedEvent> {

  Optional<FailedEvent> findByEventId(UUID eventId);

  boolean existsByEventId(UUID eventId);

  long countByStatus(FailedEventStatus status);
}
