package com.teamsolution.search.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.search.entity.ProcessedEvent;
import java.util.UUID;

public interface ProcessedEventRepository extends BaseRepository<ProcessedEvent, UUID> {}
