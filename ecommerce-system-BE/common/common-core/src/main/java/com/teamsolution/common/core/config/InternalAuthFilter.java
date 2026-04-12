package com.teamsolution.common.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.principal.UserPrincipal;
import com.teamsolution.common.core.util.UuidUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class InternalAuthFilter extends OncePerRequestFilter {

  private final String secret;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/internal")) {
      System.out.println("Level 1");

      String token = request.getHeader("X-Internal-Token");

      if (token == null || token.isBlank() || !secret.equals(token)) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(
            response.getWriter(), ApiResponse.failure("Invalid internal token"));
        return;
      }

      String accountId = request.getHeader("X-Account-Id");

      if (accountId == null || accountId.isBlank()) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure("Mising account ID"));
        return;
      } else {
        UserPrincipal userPrincipal =
            new UserPrincipal(UuidUtils.parse(accountId), null, null, null, null);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(userPrincipal, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }
    filterChain.doFilter(request, response);
  }
}
