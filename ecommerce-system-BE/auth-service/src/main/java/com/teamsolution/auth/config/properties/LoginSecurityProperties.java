package com.teamsolution.auth.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.login")
public class LoginSecurityProperties {
  private int maxAttempts;
  private int lockDurationMinutes;
  private int maxAttemptsPermanent;
}
