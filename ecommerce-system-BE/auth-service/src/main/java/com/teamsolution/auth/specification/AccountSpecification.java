package com.teamsolution.auth.specification;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.entity.AccountRole;
import com.teamsolution.auth.entity.Role;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

  public static Specification<Account> hasKeyword(String keyword) {
    return (root, query, cb) ->
        keyword == null
            ? null
            : cb.like(cb.lower(root.get("email")), "%" + keyword.toLowerCase() + "%");
  }

  public static Specification<Account> hasStatus(String status) {
    return (root, query, cb) ->
        status == null ? null : cb.equal(root.get("status"), AccountStatus.valueOf(status));
  }

  public static Specification<Account> hasRole(String roleName) {
    return (root, query, cb) -> {
      if (roleName == null) return null;
      Join<Account, AccountRole> accountRoles = root.join("accountRoles", JoinType.LEFT);
      Join<AccountRole, Role> role = accountRoles.join("role", JoinType.LEFT);
      return cb.equal(cb.lower(role.get("name")), roleName.toLowerCase());
    };
  }

  public static Specification<Account> isDeleted(Boolean isDeleted) {
    return (root, query, cb) ->
        cb.equal(root.get("isDeleted"), isDeleted != null ? isDeleted : false);
  }
}
