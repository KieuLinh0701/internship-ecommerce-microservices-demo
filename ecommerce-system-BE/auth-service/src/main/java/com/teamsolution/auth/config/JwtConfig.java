package com.teamsolution.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.teamsolution.auth.config.properties.JwtProperties;
import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

  private final JwtProperties jwtProperties;

  private RSAPublicKey rsaPublicKey;
  private RSAPrivateKey rsaPrivateKey;

  // Parses and caches RSA key pair from config on application startup
  @PostConstruct
  public void init() throws Exception {
    this.rsaPublicKey = loadPublicKey(jwtProperties.getPublicKey());
    this.rsaPrivateKey = loadPrivateKey(jwtProperties.getPrivateKey());
  }

  @Bean
  public JWKSet jwkSet() {
    RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey).keyID(jwtProperties.getKeyId()).build();
    return new JWKSet(rsaKey);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey =
        new RSAKey.Builder(rsaPublicKey)
            .privateKey(rsaPrivateKey)
            .keyID(jwtProperties.getKeyId())
            .build();
    return new ImmutableJWKSet<>(new JWKSet(rsaKey));
  }

  // Creates a JWT encoder that signs tokens using the RSA private key
  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(jwkSource());
  }

  // Creates a JWT decoder that verifies token signatures using the RSA public key
  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
  }

  // Decodes a Base64-encoded public key string into an RSAPublicKey object
  private RSAPublicKey loadPublicKey(String key) throws Exception {
    String cleanKey = key.replaceAll("\\s+", "");

    byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPublicKey) kf.generatePublic(spec);
  }

  // Decodes a Base64-encoded private key string into an RSAPrivateKey object
  private RSAPrivateKey loadPrivateKey(String key) throws Exception {
    String cleanKey = key.replaceAll("\\s+", "");

    byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPrivateKey) kf.generatePrivate(spec);
  }
}
