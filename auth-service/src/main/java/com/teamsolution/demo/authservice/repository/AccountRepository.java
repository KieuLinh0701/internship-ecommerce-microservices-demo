package com.teamsolution.demo.authservice.repository;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.common.base.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends BaseRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
}
