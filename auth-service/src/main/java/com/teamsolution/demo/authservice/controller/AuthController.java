package com.teamsolution.demo.authservice.controller;

import com.teamsolution.demo.authservice.dto.request.LoginRequest;
import com.teamsolution.demo.authservice.dto.response.AuthResponse;
import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.service.AuthJwtProvider;
import com.teamsolution.demo.authservice.service.AuthService;
import com.teamsolution.demo.common.base.BaseController;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth/accounts")
@Tag(name = "Auth", description = "Auth management APIs")
public class AuthController extends BaseController<Account, UUID> {
  public AuthController(AuthService service) {
    super(service);
  }

  @Operation(summary = "Say hello", description = "Create a new product")
  @PostMapping("/hello")
  public String sayHello() {
    return "hello";
  }
}
