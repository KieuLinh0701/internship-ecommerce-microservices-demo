package com.teamsolution.auth.service.impl;

import com.teamsolution.auth.config.properties.JwtProperties;
import com.teamsolution.auth.dto.response.AuthResponse;
import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.exception.ErrorCode;
import com.teamsolution.auth.grpc.client.CustomerGrpcClient;
import com.teamsolution.auth.service.AccountRoleService;
import com.teamsolution.auth.service.AccountService;
import com.teamsolution.auth.service.JwtTokenService;
import com.teamsolution.common.core.enums.auth.SystemRole;
import com.teamsolution.common.core.exception.AppException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

  private final JwtEncoder jwtEncoder;

  private final JwtProperties jwtProperties;

  private final AccountService accountService;
  private final AccountRoleService accountRoleService;

  private final CustomerGrpcClient customerGrpcClient;

  @Transactional
  public AuthResponse generateTokens(UUID accountId, String selectedRole) {
    Instant now = Instant.now();

    Set<String> roles = accountService.getActiveRolesByAccountId(accountId);

    if (roles.isEmpty()) {
      throw new AppException(ErrorCode.ACCOUNT_HAS_NO_ACTIVE_ROLES);
    }

    Account account = accountService.getByIdOrThrow(accountId);

    String currentRole;
    if (selectedRole == null || selectedRole.isBlank()) {
      String defaultRole = SystemRole.CUSTOMER.name();

      currentRole = roles.contains(defaultRole) ? defaultRole : roles.iterator().next();
    } else {
      if (!roles.contains(selectedRole)) {
        throw new AppException(ErrorCode.ROLE_INVALID);
      }

      currentRole = selectedRole;
    }

    String accountRoleId =
        accountRoleService.getActiveAccountRoleId(account.getId(), currentRole).toString();

    UUID customerId = null;
    if (currentRole.equals(SystemRole.CUSTOMER.name())) {
      customerId = customerGrpcClient.getCustomerIdByAccountId(account.getId());
    }

    JwtClaimsSet.Builder builder =
        JwtClaimsSet.builder()
            .issuer(jwtProperties.getIssuer())
            .subject(account.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.getAccessTokenExpiration()))
            .claim("account_id", account.getId().toString())
            .claim("account_role_id", accountRoleId)
            .claim("email", account.getEmail())
            .claim("roles", roles)
            .claim("current_role", currentRole);

    if (customerId != null) {
      builder.claim("customer_id", customerId.toString());
    }

    JwtClaimsSet accessClaims = builder.build();

    JwtClaimsSet refreshClaims =
        JwtClaimsSet.builder()
            .issuer(jwtProperties.getIssuer())
            .subject(account.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.getRefreshTokenExpiration()))
            .build();

    JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

    String accessToken =
        jwtEncoder.encode(JwtEncoderParameters.from(header, accessClaims)).getTokenValue();

    String refreshToken =
        jwtEncoder.encode(JwtEncoderParameters.from(header, refreshClaims)).getTokenValue();

    return new AuthResponse(accessToken, refreshToken);
  }
}
