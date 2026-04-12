package com.teamsolution.inventory.specification;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.constant.ProductSortField;
import com.teamsolution.inventory.entity.Product;
import jakarta.persistence.criteria.JoinType;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ProductSpecification {

  public static Specification<Product> hasStatus(ProductStatus status) {
    return (root, query, cb) -> {
      if (status == null) {
        return cb.conjunction();
      }

      return cb.equal(root.get(ProductSortField.STATUS), status);
    };
  }

  public static Specification<Product> isDeleted(Boolean isDeleted) {
    return (root, query, cb) -> {
      if (isDeleted == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get(ProductSortField.IS_DELETED), isDeleted);
    };
  }

  public static Specification<Product> hasCategoryId(UUID categoryId) {
    return (root, query, cb) -> {
      if (categoryId == null) {
        return cb.conjunction();
      }
      var categoryJoin = root.join(ProductSortField.CATEGORY, JoinType.LEFT);
      return cb.equal(categoryJoin.get(ProductSortField.ID), categoryId);
    };
  }

  public static Specification<Product> hasBrandId(UUID brandId) {
    return (root, query, cb) -> {
      if (brandId == null) {
        return cb.conjunction();
      }
      var brandJoin = root.join(ProductSortField.BRAND, JoinType.LEFT);
      return cb.equal(brandJoin.get(ProductSortField.ID), brandId);
    };
  }

  public static Specification<Product> hasKeyword(String keyword) {
    return (root, query, cb) -> {
      if (!StringUtils.hasText(keyword)) {
        return cb.conjunction();
      }

      query.distinct(true);

      String pattern = "%" + keyword.toLowerCase() + "%";

      var categoryJoin = root.join(ProductSortField.CATEGORY, JoinType.LEFT);
      var brandJoin = root.join(ProductSortField.CATEGORY, JoinType.LEFT);

      return cb.or(
          cb.like(cb.lower(root.get(ProductSortField.NAME)), pattern),
          cb.like(cb.lower(root.get(ProductSortField.DESCRIPTION)), pattern),
          cb.like(cb.lower(categoryJoin.get(ProductSortField.NAME)), pattern),
          cb.like(cb.lower(brandJoin.get(ProductSortField.NAME)), pattern));
    };
  }

  public static Specification<Product> hasPriceRange(Long minPrice, Long maxPrice) {
    return (root, query, cb) -> {
      if (minPrice == null && maxPrice == null) {
        return cb.conjunction();
      }

      if (minPrice != null && maxPrice != null) {
        return cb.between(root.get(ProductSortField.AVG_PRICE), minPrice, maxPrice);
      }

      if (minPrice != null) {
        return cb.greaterThanOrEqualTo(root.get(ProductSortField.AVG_PRICE), minPrice);
      }

      return cb.lessThanOrEqualTo(root.get(ProductSortField.AVG_PRICE), maxPrice);
    };
  }
}
