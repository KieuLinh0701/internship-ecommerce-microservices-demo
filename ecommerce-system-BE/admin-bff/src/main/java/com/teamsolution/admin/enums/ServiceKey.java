package com.teamsolution.admin.enums;

import lombok.Getter;

public enum ServiceKey {
  AUTH("auth"),
  NOTIFICATION("notification"),
  INVENTORY("inventory");

  @Getter private final String key;

  ServiceKey(String key) {
    this.key = key;
  }
}
