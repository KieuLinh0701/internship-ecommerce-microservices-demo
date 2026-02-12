package com.teamsolution.demo.authservice.config;

import com.teamsolution.demo.common.util.UuidGenerator;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegisteredClientConfig {

  // Stores OAuth2 clients in database
  @Bean
  public RegisteredClientRepository registeredClientRepository(
      JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {

    JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

    // Seed gateway-client if not exists
    if (repository.findByClientId("gateway-client") == null) {
      RegisteredClient client =
          RegisteredClient.withId(UuidGenerator.generate().toString())
              .clientId("gateway-client")
              .clientSecret(passwordEncoder.encode("gateway-secret"))
              .clientName("API Gateway Client")
              .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
              .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
              .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
              .redirectUri("http://localhost:9000/login/oauth2/code/gateway-client")
              .scope("read")
              .scope("write")
              .tokenSettings(
                  TokenSettings.builder()
                      .accessTokenTimeToLive(Duration.ofMinutes(15))
                      .refreshTokenTimeToLive(Duration.ofDays(7))
                      .reuseRefreshTokens(false)
                      .build())
              .build();

      repository.save(client);
    }

    return repository;
  }

  // Stores authorization codes and tokens in the database.
  @Bean
  public OAuth2AuthorizationService authorizationService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository clientRepository) {
    return new JdbcOAuth2AuthorizationService(jdbcTemplate, clientRepository);
  }

  // Stores user consent decisions for OAuth2 clients.
  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository clientRepository) {
    return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, clientRepository);
  }
}
