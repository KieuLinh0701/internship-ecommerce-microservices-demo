package com.teamsolution.common.kafka.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "outbox.scheduler")
public class OutboxProperties {
  private long fixedDelayMs = 5000;
  private long retryDelaySeconds = 5;
}
