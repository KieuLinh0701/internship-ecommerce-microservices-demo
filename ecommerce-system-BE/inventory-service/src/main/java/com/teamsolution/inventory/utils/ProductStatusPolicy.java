package com.teamsolution.inventory.utils;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.enums.ProductImageStatus;
import com.teamsolution.inventory.enums.ProductVariantInventoryStatus;
import com.teamsolution.inventory.enums.ProductVariantStatus;

public class ProductStatusPolicy {
  public static ProductVariantStatus mapVariantStatus(ProductStatus productStatus) {
    return switch (productStatus) {
      case ACTIVE -> ProductVariantStatus.ACTIVE;
      case INACTIVE -> ProductVariantStatus.INACTIVE;
      case DRAFT -> ProductVariantStatus.DRAFT;
      case DELETED -> ProductVariantStatus.DELETED;
    };
  }

  public static ProductImageStatus mapImageStatus(ProductStatus productStatus) {
    return switch (productStatus) {
      case ACTIVE -> ProductImageStatus.ACTIVE;
      case INACTIVE -> ProductImageStatus.INACTIVE;
      case DRAFT -> ProductImageStatus.DRAFT;
      case DELETED -> ProductImageStatus.DELETED;
    };
  }

  public static ProductVariantInventoryStatus mapInventoryStatus(ProductStatus productStatus) {
    return switch (productStatus) {
      case ACTIVE -> ProductVariantInventoryStatus.ACTIVE;
      default -> ProductVariantInventoryStatus.FROZEN;
    };
  }
}
