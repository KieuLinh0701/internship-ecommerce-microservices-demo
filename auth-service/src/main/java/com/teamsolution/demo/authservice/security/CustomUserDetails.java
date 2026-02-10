package com.teamsolution.demo.authservice.security;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.enums.AccountRoleStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Account account;

    public CustomUserDetails(Account account) {
        this.account = account;
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (account.getAccountRoles() == null) {
            return List.of();
        }

        return account.getAccountRoles().stream()
                .filter(ar -> ar.getStatus() == AccountRoleStatus.ACTIVE)
                .map(ar -> new SimpleGrantedAuthority("ROLE_" + ar.getRole().getName()))
                .toList();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
