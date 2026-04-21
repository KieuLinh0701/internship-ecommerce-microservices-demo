package com.teamsolution.cart.repository;

import com.teamsolution.cart.entity.FailedEvent;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface FailedEventRepository
    extends BaseRepository<FailedEvent, UUID>, JpaSpecificationExecutor<FailedEvent> {

  Optional<FailedEvent> findByEventId(UUID eventId);

  boolean existsByEventId(UUID eventId);

  long countByStatus(FailedEventStatus status);
}
