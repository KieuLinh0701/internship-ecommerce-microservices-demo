package com.teamsolution.inventory.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "temp-image-cleanup")
public class TempImageCleanupProperties {
  private long fixedRateMs;
  private int ttlMinutes;
}
