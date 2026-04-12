package com.teamsolution.common.core.enums.auth;

public enum AccountStatus {
  PENDING,
  ACTIVE,
  INACTIVE,
  SUSPENDED,
  DELETED;

  public boolean isVerifiable() {
    return this == PENDING;
  }

  public boolean isActive() {
    return this == ACTIVE;
  }

  public boolean isSuspended() {
    return this == SUSPENDED;
  }

  public boolean isEligibleForRegistration() {
    return this == ACTIVE || this == INACTIVE;
  }
}
