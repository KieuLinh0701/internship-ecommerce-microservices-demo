package com.teamsolution.notification.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "websocket")
@Getter
@Setter
public class WebSocketProperties {
  private String allowedOrigins;
}
