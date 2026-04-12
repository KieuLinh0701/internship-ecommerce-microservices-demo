package com.teamsolution.admin.config;

import com.teamsolution.admin.exception.ErrorCode;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.security.SecurityUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  @LoadBalanced
  public RestClient.Builder loadBalancedRestClientBuilder(
      @Value("${internal.secret}") String secret) {
    return RestClient.builder()
        .defaultHeader("X-Internal-Token", secret)
        .requestInitializer(
            request -> {
              UUID accountId = SecurityUtils.getCurrentAccountId();

              if (accountId == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
              }
              request.getHeaders().add("X-Account-Id", accountId.toString());
            });
  }

  @Bean
  public RestClient restClient(RestClient.Builder builder) {
    return builder.build();
  }
}
