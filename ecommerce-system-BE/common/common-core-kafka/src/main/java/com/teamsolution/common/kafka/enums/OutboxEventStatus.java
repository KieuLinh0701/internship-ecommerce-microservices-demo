package com.teamsolution.common.kafka.enums;

public enum OutboxEventStatus {
  PENDING,
  IN_PROGRESS,
  SENT,
  FAILED
}
