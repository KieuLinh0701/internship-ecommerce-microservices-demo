package com.teamsolution.admin.config.properties;

import com.teamsolution.admin.enums.ServiceKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumer")
@Data
public class ConsumerServicesProperties {

  private Map<String, String> services = new HashMap<>();
  private List<String> failedEventServices = new ArrayList<>();
  private long timeout = 3;

  public String getServiceId(ServiceKey key) {
    return services.get(key.getKey());
  }
}
