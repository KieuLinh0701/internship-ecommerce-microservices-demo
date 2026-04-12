package com.teamsolution.inventory.specification;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.enums.ProductImageStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ProductImageSpecification {

  public static Specification<ProductImage> hasProductId(UUID productId) {
    return (root, query, cb) ->
        productId == null ? null : cb.equal(root.get("product").get("id"), productId);
  }

  public static Specification<ProductImage> inStatuses(List<ProductImageStatus> statuses) {
    return (root, query, cb) ->
        (statuses == null || statuses.isEmpty()) ? null : root.get("status").in(statuses);
  }

  public static Specification<ProductImage> inReasons(List<StatusChangeReason> reasons) {
    return (root, query, cb) ->
        (reasons == null || reasons.isEmpty()) ? null : root.get("statusChangeReason").in(reasons);
  }
}
