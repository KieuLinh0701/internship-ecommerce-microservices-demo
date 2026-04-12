package com.teamsolution.inventory.specification;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.inventory.entity.ProductVariant;
import com.teamsolution.inventory.enums.ProductVariantStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ProductVariantSpecification {

  public static Specification<ProductVariant> hasProductId(UUID productId) {
    return (root, query, cb) ->
        productId == null ? null : cb.equal(root.get("product").get("id"), productId);
  }

  public static Specification<ProductVariant> inStatuses(List<ProductVariantStatus> statuses) {
    return (root, query, cb) ->
        (statuses == null || statuses.isEmpty()) ? null : root.get("status").in(statuses);
  }

  public static Specification<ProductVariant> inReasons(List<StatusChangeReason> reasons) {
    return (root, query, cb) ->
        (reasons == null || reasons.isEmpty()) ? null : root.get("statusChangeReason").in(reasons);
  }
}
