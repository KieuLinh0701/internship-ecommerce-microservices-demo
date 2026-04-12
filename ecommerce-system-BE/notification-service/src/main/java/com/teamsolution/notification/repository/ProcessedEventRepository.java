package com.teamsolution.notification.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.notification.entity.ProcessedEvent;
import java.util.UUID;

public interface ProcessedEventRepository extends BaseRepository<ProcessedEvent, UUID> {}
