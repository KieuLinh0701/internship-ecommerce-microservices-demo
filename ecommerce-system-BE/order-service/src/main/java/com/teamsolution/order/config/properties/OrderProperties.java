package com.teamsolution.order.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "order")
@Getter
@Setter
public class OrderProperties {
  private long paymentTimeoutHours;
}
