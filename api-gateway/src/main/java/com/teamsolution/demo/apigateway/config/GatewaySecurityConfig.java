package com.teamsolution.demo.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            exchanges ->
                exchanges
                    // Public endpoints
                    .pathMatchers("/auth-service/auth/**")
                    .permitAll()
                    .pathMatchers("/auth-service/oauth2/**")
                    .permitAll()
                    .pathMatchers("/auth-service/.well-known/**")
                    .permitAll()

                    // All other requests need
                    .anyExchange().authenticated())

        // The gateway only validates the JWT and does NOT check authorities.
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder())));

    // CORS configuration for the frontend UI
    //        http.cors(cors -> cors.configurationSource(request -> {
    //            CorsConfiguration config = new CorsConfiguration();
    //            config.setAllowedOrigins(List.of("http://localhost:3000"));
    //            config.setAllowedMethods(List.of("*"));
    //            config.setAllowedHeaders(List.of("*"));
    //            return config;
    //        }));
    return http.build();
  }

  @Bean
  public ReactiveJwtDecoder reactiveJwtDecoder() {
    return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }
}
