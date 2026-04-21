package com.teamsolution.order.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.order.entity.ProcessedEvent;
import java.util.UUID;

public interface ProcessedEventRepository extends BaseRepository<ProcessedEvent, UUID> {}
