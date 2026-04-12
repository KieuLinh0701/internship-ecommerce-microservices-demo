package com.teamsolution.common.core.security;

import com.teamsolution.common.core.security.principal.UserPrincipal;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  private SecurityUtils() {}

  public static UserPrincipal getCurrentUser() {
    Authentication auth = getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      return null;
    }
    if (auth.getPrincipal() instanceof UserPrincipal userPrincipal) {
      return userPrincipal;
    }

    return null;
  }

  public static UUID getCurrentAccountId() {
    UserPrincipal userPrincipal = getCurrentUser();
    return userPrincipal != null ? userPrincipal.accountId() : null;
  }

  public static UUID getCurrentAccountRoleId() {
    UserPrincipal user = getCurrentUser();
    return user != null ? user.accountRoleId() : null;
  }

  public static UUID getCurrentCustomerId() {
    UserPrincipal user = getCurrentUser();
    return user != null ? user.customerId() : null;
  }

  public static String getCurrentEmail() {
    UserPrincipal user = getCurrentUser();
    return user != null ? user.email() : null;
  }

  public static String getCurrentRoleName() {
    UserPrincipal user = getCurrentUser();
    return user != null ? user.currentRoleName() : null;
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
