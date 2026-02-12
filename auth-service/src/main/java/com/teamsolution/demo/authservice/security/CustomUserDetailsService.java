package com.teamsolution.demo.authservice.security;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.enums.AccountRoleStatus;
import com.teamsolution.demo.authservice.enums.AccountStatus;
import com.teamsolution.demo.authservice.enums.RoleStatus;
import com.teamsolution.demo.authservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Account account =
        accountRepository
            .findByEmailWithActiveRoles(
                email, AccountStatus.ACTIVE, AccountRoleStatus.ACTIVE, RoleStatus.ACTIVE)
            .orElseThrow(() -> new UsernameNotFoundException("User not found or inactive"));

    return new CustomUserDetails(account);
  }
}
