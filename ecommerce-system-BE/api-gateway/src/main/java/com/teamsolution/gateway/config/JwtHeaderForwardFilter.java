package com.teamsolution.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtHeaderForwardFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return exchange
        .getPrincipal()
        .cast(JwtAuthenticationToken.class)
        .flatMap(
            auth -> {
              Jwt jwt = auth.getToken();

              ServerHttpRequest newRequest =
                  new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public HttpHeaders getHeaders() {
                      HttpHeaders headers = new HttpHeaders();
                      headers.addAll(super.getHeaders());
                      headers.set("X-Account-Id", jwt.getClaimAsString("account_id"));
                      headers.set("X-Role", jwt.getClaimAsString("current_role"));
                      headers.set("X-Account-Role-id", jwt.getClaimAsString("account_role_id"));
                      headers.set("X-Customer-Id", jwt.getClaimAsString("customer_id"));
                      headers.set("X-Email", jwt.getClaimAsString("email"));
                      return headers;
                    }
                  };

              return chain.filter(exchange.mutate().request(newRequest).build());
            })
        .switchIfEmpty(chain.filter(exchange));
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
