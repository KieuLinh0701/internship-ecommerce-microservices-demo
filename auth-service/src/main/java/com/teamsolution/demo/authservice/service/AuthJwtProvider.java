package com.teamsolution.demo.authservice.service;

import com.teamsolution.demo.authservice.entity.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthJwtProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-expiration-ms}")
  private long accessExpirationMs;

  @Value("${jwt.refresh-expiration-ms}")
  private long refreshExpirationMs;

  private SecretKey getSigningKey() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(String subject, List<String> roles) {
    return Jwts.builder()
            .subject(subject)
            .claim("role", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
            .signWith(getSigningKey())
            .compact();
  }

  public String generateRefreshToken(String subject) {
    return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
            .signWith(getSigningKey())
            .compact();
  }
}
