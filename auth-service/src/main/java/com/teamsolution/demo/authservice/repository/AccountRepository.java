package com.teamsolution.demo.authservice.repository;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.common.base.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends BaseRepository<Account, Long> {
}
