package com.teamsolution.gateway.config;

import com.teamsolution.gateway.config.properties.AuthServiceProperties;
import com.teamsolution.gateway.security.GoogleOAuth2SuccessHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

  private final AuthServiceProperties authServiceProperties;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http, GoogleOAuth2SuccessHandler successHandler) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            exchanges ->
                exchanges
                    // Public endpoints
                    .pathMatchers("/api/search/**")
                    .permitAll()

                    // Auth public endpoints
                    .pathMatchers(
                        HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/refresh",
                        "/api/auth/verify-email",
                        "/api/auth/resend-verification-otp",
                        "/api/auth/oauth2/google",
                        "/api/auth/password-reset/send-otp",
                        "/api/auth/password-reset/resend-otp",
                        "/api/auth/password-reset/verify",
                        "/api/auth/password-reset/reset")
                    .permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/auth/jwks")
                    .permitAll()

                    // Inventory Public
                    .pathMatchers(HttpMethod.GET, "/api/inventory/**")
                    .permitAll()

                    // Payment Public
                    .pathMatchers(HttpMethod.GET, "/api/payments/ipn")
                    .permitAll()

                    // OAuth2
                    .pathMatchers("/login/oauth2/code/**", "/oauth2/**")
                    .permitAll()

                    // Swagger
                    .pathMatchers(
                        "/api/*/swagger-ui/**",
                        "/api/*/swagger-ui.html",
                        "/api/*/webjars/**",
                        "/api/*/v3/api-docs/**",
                        "/api/*/v3/api-docs",
                        "/webjars/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**")
                    .permitAll()

                    // All other
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .oauth2Login(oauth2 -> oauth2.authenticationSuccessHandler(successHandler));

    // CORS configuration for the frontend UI
    http.cors(
        cors ->
            cors.configurationSource(
                request -> {
                  CorsConfiguration config = new CorsConfiguration();
                  config.setAllowedOrigins(List.of("http://localhost:3000"));
                  config.setAllowedMethods(List.of("*"));
                  config.setAllowedHeaders(List.of("*"));
                  config.setAllowCredentials(true);
                  return config;
                }));
    return http.build();
  }

  @Bean
  public ReactiveJwtDecoder reactiveJwtDecoder(@LoadBalanced WebClient.Builder webClientBuilder) {
    WebClient webClient = webClientBuilder.build();

    return NimbusReactiveJwtDecoder.withJwkSetUri(authServiceProperties.getJwksUri())
        .webClient(webClient)
        .build();
  }
}
