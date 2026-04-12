package com.teamsolution.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwksController {

  private final JWKSet jwkSet;

  @GetMapping("/jwks")
  public Map<String, Object> jwks() {
    return jwkSet.toJSONObject();
  }
}
