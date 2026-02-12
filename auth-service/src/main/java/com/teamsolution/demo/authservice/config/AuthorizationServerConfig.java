package com.teamsolution.demo.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig {

  @Value("${spring.security.oauth2.authorization-server.issuer-uri}")
  private String issuerUri;

  // OAuth2 Authorization Server Security Filter Chain
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
      throws Exception {

    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    http.exceptionHandling(
        exceptions ->
            exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

    return http.build();
  }

  // Authorization Server Settings (Issuer URL)
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer(issuerUri).build();
  }
}
