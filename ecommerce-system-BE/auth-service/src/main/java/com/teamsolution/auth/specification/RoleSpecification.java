package com.teamsolution.auth.specification;

import com.teamsolution.auth.entity.Role;
import com.teamsolution.common.core.enums.auth.RoleStatus;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecification {

  public static Specification<Role> hasName(String name) {
    return (root, query, cb) ->
        name == null
            ? null
            : cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
  }

  public static Specification<Role> hasStatus(RoleStatus status) {
    return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
  }

  public static Specification<Role> isDeleted(Boolean isDeleted) {
    return (root, query, cb) -> cb.equal(root.get("isDeleted"), isDeleted != null && isDeleted);
  }
}
