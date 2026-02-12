package com.teamsolution.demo.authservice.repository;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.enums.AccountRoleStatus;
import com.teamsolution.demo.authservice.enums.AccountStatus;
import com.teamsolution.demo.authservice.enums.RoleStatus;
import com.teamsolution.demo.common.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends BaseRepository<Account, UUID> {
  Optional<Account> findByEmail(String email);

  @Query("""
    SELECT DISTINCT a
    FROM Account a
    LEFT JOIN FETCH a.accountRoles ar
    LEFT JOIN FETCH ar.role r
    WHERE a.email = :email
      AND a.status = :accountStatus
      AND ar.status = :accountRoleStatus
      AND r.status = :roleStatus
""")
  Optional<Account> findByEmailWithActiveRoles(
          @Param("email") String email,
          @Param("accountStatus") AccountStatus accountStatus,
          @Param("accountRoleStatus") AccountRoleStatus accountRoleStatus,
          @Param("roleStatus") RoleStatus roleStatus
  );
}
