package com.teamsolution.notification.repository;

import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.notification.entity.FailedEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FailedEventRepository
    extends BaseRepository<FailedEvent, UUID>, JpaSpecificationExecutor<FailedEvent> {

  Optional<FailedEvent> findByEventId(UUID eventId);

  boolean existsByEventId(UUID eventId);

  long countByStatus(FailedEventStatus status);
}
