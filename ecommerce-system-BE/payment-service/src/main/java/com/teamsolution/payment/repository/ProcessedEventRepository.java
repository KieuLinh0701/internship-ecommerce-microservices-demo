package com.teamsolution.payment.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.payment.entity.ProcessedEvent;

import java.util.UUID;

public interface ProcessedEventRepository extends BaseRepository<ProcessedEvent, UUID> {}
