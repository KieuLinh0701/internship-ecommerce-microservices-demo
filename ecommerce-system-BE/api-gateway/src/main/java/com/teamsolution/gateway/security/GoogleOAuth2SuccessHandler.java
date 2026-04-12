package com.teamsolution.gateway.security;

import com.teamsolution.gateway.config.properties.AuthServiceProperties;
import com.teamsolution.gateway.dto.request.GoogleLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {

  private final WebClient.Builder webClientBuilder;

  private final AuthServiceProperties properties;

  @Override
  public Mono<Void> onAuthenticationSuccess(
      WebFilterExchange exchange, Authentication authentication) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    GoogleLoginRequest loginRequest =
        new GoogleLoginRequest(
            oAuth2User.getAttribute("email"),
            oAuth2User.getAttribute("name"),
            oAuth2User.getAttribute("picture"));

    return webClientBuilder
        .build()
        .post()
        .uri(properties.getGoogleLoginUri())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginRequest)
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(
            tokenResponse -> {
              exchange
                  .getExchange()
                  .getResponse()
                  .getHeaders()
                  .setContentType(MediaType.APPLICATION_JSON);
              var buffer =
                  exchange
                      .getExchange()
                      .getResponse()
                      .bufferFactory()
                      .wrap(tokenResponse.getBytes());
              return exchange.getExchange().getResponse().writeWith(Mono.just(buffer));
            });
  }
}
