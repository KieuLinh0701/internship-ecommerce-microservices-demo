package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.ProcessedEvent;
import java.util.UUID;

public interface ProcessedEventRepository extends BaseRepository<ProcessedEvent, UUID> {}
