package com.teamsolution.demo.authservice.config.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.clients.gateway")
public class RegisteredClientProperties {
  private String clientId;
  private String clientSecret;
  private String clientName;
  private String redirectUri;
  private Duration accessTokenTtl;
  private Duration refreshTokenTtl;
}
