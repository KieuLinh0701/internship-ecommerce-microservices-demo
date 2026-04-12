package com.teamsolution.auth.repository;

import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.auth.entity.Role;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRoleRepository extends BaseRepository<AccountRole, UUID> {

  @Modifying
  @Query(
      "UPDATE AccountRole ar SET ar.status = 'ACTIVE' WHERE ar.account.id = :accountId AND ar.status = 'PENDING'")
  void activateByAccountId(@Param("accountId") UUID accountId);

  @Query(
      """
                        SELECT ar.role
                        FROM AccountRole ar
                        WHERE ar.account.id = :accountId
                            AND ar.account.isDeleted = false
                            AND ar.status = :accountRoleStatus
                            AND ar.isDeleted = false
                            AND ar.role.status = :roleStatus
                            AND ar.role.isDeleted = false
                    """)
  Set<Role> findActiveRolesByAccountId(
      @Param("accountId") UUID accountId,
      @Param("accountRoleStatus") AccountRoleStatus accountRoleStatus,
      @Param("roleStatus") RoleStatus roleStatus);

  @Query(
      """
                SELECT ar FROM AccountRole ar
                JOIN ar.role r
                WHERE ar.account.id = :accountId
                    AND ar.account.isDeleted = false
                    AND r.name = :roleName
                    AND ar.status = 'ACTIVE'
                    AND ar.isDeleted = false
                    AND r.status = 'ACTIVE'
                    AND ar.role.isDeleted = false
            """)
  Optional<AccountRole> findActiveByAccountIdAndRoleName(
      @Param("accountId") UUID accountId, @Param("roleName") String roleName);

  List<AccountRole> findAllByRoleIdAndStatus(UUID roleId, RoleStatus status);

  @Query(
      """
                SELECT ar FROM AccountRole ar
                JOIN ar.role r
                WHERE ar.account.id = :accountId
                    AND ar.account.isDeleted = false
                    AND ar.account.status = 'ACTIVE'
                    AND ar.role.id = :roleId
                    AND ar.role.isDeleted = false
                    AND ar.role.status = 'ACTIVE'
                    AND ar.status = 'ACTIVE'
                    AND ar.isDeleted = false
            """)
  Optional<AccountRole> findActiveByAccountIdAndRoleId(
      @Param("accountId") UUID accountId, @Param("roleId") UUID roleId);

  Optional<AccountRole> findByAccount_IdAndStatusAndRole_NameAndIsDeleted(
      UUID accountId, AccountRoleStatus status, String roleName, boolean isDeleted);

  List<AccountRole> findByAccount_IdAndStatusAndIsDeleted(
      UUID accountId, AccountRoleStatus status, boolean isDeleted);

  @Query(
      """
                SELECT ar FROM AccountRole ar
                JOIN ar.role r
                WHERE ar.account.id = :accountId
                    AND r.name = :roleName
                    AND ar.account.isDeleted = false
                    AND r.isDeleted = false
            """)
  Optional<AccountRole> findByAccountIdAndRoleName(
      @Param("accountId") UUID accountId, @Param("roleName") String roleName);
}
