package com.teamsolution.demo.authservice.controller;

import com.teamsolution.demo.authservice.dto.request.LoginRequest;
import com.teamsolution.demo.authservice.dto.response.AuthResponse;
import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.service.AuthJwtProvider;
import com.teamsolution.demo.authservice.service.AuthService;
import com.teamsolution.demo.common.base.BaseController;

import java.util.List;
import java.util.UUID;

import com.teamsolution.demo.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth/accounts")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final AuthJwtProvider jwtProvider;

  @PostMapping("/login")
  public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
            )
    );

    UserDetails user = (UserDetails) authentication.getPrincipal();
    List<String> roles = user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    String accessToken = jwtProvider.generateAccessToken(user.getUsername(), roles);
    String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

    return ApiResponse.success(new AuthResponse(accessToken, refreshToken));
  }
}
