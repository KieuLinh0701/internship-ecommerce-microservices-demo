package com.teamsolution.auth.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.otp")
public class OtpSecurityProperties {

  private int maxResend;
  private int cooldownSeconds;
  private int expiresInSeconds;
  int sessionExpiresInSeconds;
  private int maxAttempts;
}
