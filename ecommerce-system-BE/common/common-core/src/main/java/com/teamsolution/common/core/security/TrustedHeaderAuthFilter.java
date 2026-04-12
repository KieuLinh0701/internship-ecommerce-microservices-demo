package com.teamsolution.common.core.security;

import com.teamsolution.common.core.security.principal.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TrustedHeaderAuthFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/internal")) {
      System.out.println("Level 2");
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      System.out.println("Level 3");
      filterChain.doFilter(request, response);
      return;
    }

    String accountId = request.getHeader("X-Account-Id");
    String role = request.getHeader("X-Role");
    String accountRoleId = request.getHeader("X-Account-Role-Id");
    String customerId = request.getHeader("X-Customer-Id");
    String email = request.getHeader("X-Email");

    if (accountId != null && role != null) {
      List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + role));

      UUID accountUuid = UUID.fromString(accountId);
      UUID accountRoleUuid = UUID.fromString(accountRoleId);
      UUID customerUuid = customerId != null ? UUID.fromString(customerId) : null;

      UserPrincipal userPrincipal =
          new UserPrincipal(accountUuid, accountRoleUuid, customerUuid, email, role);

      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userPrincipal, null, authorityList);

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}
