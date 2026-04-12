package com.teamsolution.gateway.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "auth-service")
@Component
@Getter
@Setter
public class AuthServiceProperties {
  private String jwksUri;
  private String googleLoginUri;
}
