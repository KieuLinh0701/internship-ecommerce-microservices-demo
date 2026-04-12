package com.teamsolution.auth.security;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final AccountService accountService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Account account =
        accountService
            .findByEmailWithRoles(email)
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

    return new CustomUserDetails(account);
  }
}
