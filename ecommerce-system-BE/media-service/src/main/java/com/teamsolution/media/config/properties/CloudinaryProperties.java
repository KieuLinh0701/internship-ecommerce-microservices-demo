package com.teamsolution.media.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {

  private String cloudName;
  private String apiKey;
  private String apiSecret;
  private Map<String, String> folders;
}
