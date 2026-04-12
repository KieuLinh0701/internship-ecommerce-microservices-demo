package com.teamsolution.inventory.specification;

import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.enums.BrandStatus;
import org.springframework.data.jpa.domain.Specification;

public class BrandSpecification {

  public static Specification<Brand> hasStatus(BrandStatus status) {
    return (root, query, cb) -> {
      if (status == null) {
        return cb.conjunction();
      }

      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<Brand> notDeleted(Boolean isDeleted) {
    return (root, query, cb) -> {
      if (isDeleted == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("isDeleted"), isDeleted);
    };
  }

  public static Specification<Brand> hasKeyword(String keyword) {
    return (root, query, cb) -> {
      if (!org.springframework.util.StringUtils.hasText(keyword)) {
        return cb.conjunction();
      }

      String pattern = "%" + keyword.toLowerCase() + "%";

      return cb.or(
          cb.like(cb.lower(root.get("name")), pattern),
          cb.like(cb.lower(root.get("slug")), pattern));
    };
  }
}
