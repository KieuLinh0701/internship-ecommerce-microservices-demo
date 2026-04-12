package com.teamsolution.common.kafka.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

  private Dlt dlt = new Dlt();
  private Retry retry = new Retry();

  @Getter
  @Setter
  public static class Dlt {

    private String suffix = ".DLT";
  }

  @Getter
  @Setter
  public static class Retry {

    private long initialIntervalMs = 2000;
    private double multiplier = 2.0;
    private int maxKafkaRetry = 3;
    private int maxManualRetry = 5;
  }
}
