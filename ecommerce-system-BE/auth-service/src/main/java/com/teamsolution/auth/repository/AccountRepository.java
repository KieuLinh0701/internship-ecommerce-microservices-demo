package com.teamsolution.auth.repository;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.common.jpa.repository.BaseSoftDeleteRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository
    extends BaseSoftDeleteRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
  Optional<Account> findByEmailAndIsDeletedFalse(String email);

  Optional<Account> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query(
      """
                  SELECT DISTINCT a
                  FROM Account a
                  LEFT JOIN FETCH a.accountRoles ar
                  LEFT JOIN FETCH ar.role r
                  WHERE a.id = :accountId
                    AND a.status = 'ACTIVE'
                    AND a.isDeleted = false
                    AND ar.status = 'ACTIVE'
                    AND ar.isDeleted = false
                    AND r.status = 'ACTIVE'
                    AND r.isDeleted = false
              """)
  Optional<Account> findByAccountIdWithActiveRoles(@Param("accountId") UUID accountId);

  @Query(
      """
                SELECT a FROM Account a
                LEFT JOIN FETCH a.accountRoles ar
                LEFT JOIN FETCH ar.role r
                WHERE a.email = :email
                  AND a.isDeleted = false
                  AND ar.status = 'ACTIVE'
                  AND ar.isDeleted = false
                  AND r.status = 'ACTIVE'
                  AND r.isDeleted = false
            """)
  Optional<Account> findByEmailWithRoles(String email);
}
