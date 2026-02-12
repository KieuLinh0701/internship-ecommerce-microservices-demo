package com.teamsolution.demo.authservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.teamsolution.demo.authservice.security.CustomUserDetails;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Slf4j
@Configuration
public class JwtConfig {

  @Value("${jwt.public-key}")
  private String publicKeyBase64;

  @Value("${jwt.private-key}")
  private String privateKeyBase64;

  // JWK Source - RSA Public/Private Key Pair
  @Bean
  public JWKSource<SecurityContext> jwkSource() throws Exception {
    RSAPublicKey publicKey = loadPublicKey(publicKeyBase64);
    RSAPrivateKey privateKey = loadPrivateKey(privateKeyBase64);

    RSAKey rsaKey =
        new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("auth-service-key-1").build();

    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
  }

  // JWT Token Customizer - Add custom claims
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(
      UserDetailsService userDetailsService) {

    return context -> {
      if (context.getTokenType().getValue().equals("access_token")) {
        Authentication authentication = context.getPrincipal();

        CustomUserDetails userDetails =
            (CustomUserDetails) userDetailsService.loadUserByUsername(authentication.getName());

        // Add custom claims
        context.getClaims().claim("account_id", userDetails.getAccountId());
        context.getClaims().claim("email", userDetails.getUsername());
        context
            .getClaims()
            .claim(
                "roles",
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
      }
    };
  }

  // Load RSA public key from Base64 string
  private RSAPublicKey loadPublicKey(String key) throws Exception {
    byte[] keyBytes = Base64.getDecoder().decode(key);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPublicKey) kf.generatePublic(spec);
  }

  // Load RSA private key from Base64 string
  private RSAPrivateKey loadPrivateKey(String key) throws Exception {
    byte[] keyBytes = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPrivateKey) kf.generatePrivate(spec);
  }
}
