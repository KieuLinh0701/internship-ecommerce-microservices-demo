package com.teamsolution.demo.authservice.service;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.repository.AccountRepository;
import com.teamsolution.demo.common.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService extends BaseServiceImpl<Account, UUID> {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder) {
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account login(String email, String rawPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return account;
    }
}