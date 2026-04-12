package com.teamsolution.auth.security;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(Account account) implements UserDetails {

  public UUID getAccountId() {
    return account.getId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<AccountRole> accountRoles = account.getAccountRoles();

    if (accountRoles == null) {
      return Set.of();
    }

    return accountRoles.stream()
        .map(ar -> new SimpleGrantedAuthority(ar.getRole().getName()))
        .collect(Collectors.toSet());
  }

  @Override
  public String getPassword() {
    return account.getPassword();
  }

  @Override
  public String getUsername() {
    return account.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return account.getStatus() != AccountStatus.SUSPENDED;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return account.getStatus() != AccountStatus.INACTIVE;
  }
}
