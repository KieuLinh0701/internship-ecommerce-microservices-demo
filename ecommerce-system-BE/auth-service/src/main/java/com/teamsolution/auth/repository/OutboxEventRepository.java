package com.teamsolution.auth.repository;

import com.teamsolution.auth.entity.OutboxEvent;
import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.common.kafka.enums.OutboxEventStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends BaseRepository<OutboxEvent, UUID> {

  List<OutboxEvent> findTop100ByStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
      OutboxEventStatus status, LocalDateTime now);
}
