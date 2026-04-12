package com.teamsolution.search.entity;

import com.teamsolution.common.kafka.entity.BaseProcessedEventEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "processed_events")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ProcessedEvent extends BaseProcessedEventEntity {}
