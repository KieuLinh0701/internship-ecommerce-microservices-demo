package com.teamsolution.inventory.service.internal;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.entity.ProductVariant;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.enums.ProductVariantStatus;
import java.util.List;
import java.util.UUID;

public interface ProductVariantInternalService {

  List<ProductVariant> getByProductId(UUID id);

  List<ProductVariant> saveAll(List<ProductVariant> variants);

  boolean existsBySku(String sku);

  void updateStatus(
      UUID productId,
      List<ProductVariantStatus> requestStatuses,
      List<StatusChangeReason> requestReasons,
      ProductStatus productStatus,
      StatusChangeReason reason,
      UUID adminId,
      InventoryEventType eventType);
}
