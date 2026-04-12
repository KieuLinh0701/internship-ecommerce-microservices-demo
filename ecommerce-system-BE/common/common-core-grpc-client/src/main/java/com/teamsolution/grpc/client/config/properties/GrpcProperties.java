package com.teamsolution.grpc.client.config.properties;

import jakarta.validation.constraints.Min;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "grpc")
public class GrpcProperties {

  private Retry retry;
  private Duration timeout;

  @Data
  public static class Retry {

    @Min(1)
    private int maxAttempts;

    private long baseDelayMs;
  }
}
