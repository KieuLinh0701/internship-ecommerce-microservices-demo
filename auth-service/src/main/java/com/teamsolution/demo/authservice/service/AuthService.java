package com.teamsolution.demo.authservice.service;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.repository.AccountRepository;
import com.teamsolution.demo.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends BaseServiceImpl<Account, Long> {
    public AuthService(AccountRepository repository) {
        super(repository);
    }
}
