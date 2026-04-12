package com.teamsolution.inventory.service.internal;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.dto.response.product.detail.ProductImageResponse;
import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.enums.ProductImageStatus;
import java.util.List;
import java.util.UUID;

public interface ProductImageInternalService {

  List<ProductImageResponse> getByProductId(UUID productId);

  List<ProductImage> saveAll(List<ProductImage> images);

  List<ProductImage> findValidTempImages(List<UUID> ids);

  List<ProductImage> findValidImagesByProductIds(List<UUID> ids);

  void updateStatus(
      UUID productId,
      List<ProductImageStatus> requestStatuses,
      List<StatusChangeReason> requestReasons,
      ProductStatus productStatus,
      StatusChangeReason reason,
      UUID adminId,
      InventoryEventType eventType);

  // scheduler
  int cleanupTempImages(long olderThanMinutes);
}
