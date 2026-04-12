package com.teamsolution.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.common.core.config.InternalAuthFilter;
import com.teamsolution.common.core.enums.auth.SystemRole;
import com.teamsolution.common.core.security.TrustedHeaderAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${internal.secret}")
  private String internalSecret;

  private final ObjectMapper objectMapper;

  private final TrustedHeaderAuthFilter trustedHeaderAuthFilter;

  // Default Security Filter Chain: Handles form login and basic authentication
  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/register",
                        "/login",
                        "/refresh",
                        "/verify-email",
                        "/jwks",
                        "/resend-verification-otp",
                        "/oauth2/google",
                        "/password-reset/send-otp",
                        "/password-reset/resend-otp",
                        "/password-reset/verify",
                        "/password-reset/reset")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole(SystemRole.ADMIN.name())
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .requestMatchers(
                        "/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(
            new InternalAuthFilter(internalSecret, objectMapper) {
              @Override
              protected boolean shouldNotFilter(HttpServletRequest request) {
                return request.getRequestURI().equals("/oauth2/google");
              }
            },
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(trustedHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
