package com.teamsolution.inventory.service.internal.impl;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.entity.ProductVariant;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.enums.ProductVariantInventoryStatus;
import com.teamsolution.inventory.enums.ProductVariantStatus;
import com.teamsolution.inventory.repository.ProductVariantRepository;
import com.teamsolution.inventory.service.internal.ProductVariantInternalService;
import com.teamsolution.inventory.specification.ProductVariantSpecification;
import com.teamsolution.inventory.utils.ProductStatusPolicy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantInternalServiceImpl implements ProductVariantInternalService {

  private final ProductVariantRepository productVariantRepository;

  @Override
  public List<ProductVariant> getByProductId(UUID productId) {
    return productVariantRepository.findVariantsByProductId(productId);
  }

  @Override
  public List<ProductVariant> saveAll(List<ProductVariant> variants) {
    return productVariantRepository.saveAll(variants);
  }

  @Override
  public boolean existsBySku(String sku) {
    return productVariantRepository.existsBySku(sku);
  }

  @Override
  public void updateStatus(
      UUID productId,
      List<ProductVariantStatus> requestStatuses,
      List<StatusChangeReason> requestReasons,
      ProductStatus productStatus,
      StatusChangeReason reason,
      UUID adminId,
      InventoryEventType eventType) {

    Specification<ProductVariant> spec =
        Specification.where(ProductVariantSpecification.hasProductId(productId))
            .and(ProductVariantSpecification.inReasons(requestReasons))
            .and(ProductVariantSpecification.inStatuses(requestStatuses));

    List<ProductVariant> variants = productVariantRepository.findAll(spec);

    for (ProductVariant v : variants) {

      ProductVariantStatus newVariantStatus = ProductStatusPolicy.mapVariantStatus(productStatus);

      switch (eventType) {
        case InventoryEventType.DELETE_PRODUCT -> {
          v.setIsDeleted(true);
          v.setDeletedAt(LocalDateTime.now());
          v.setDeletedBy(adminId);
          v.setStatus(newVariantStatus);
        }

        case InventoryEventType.RESTORE_PRODUCT -> {
          v.setIsDeleted(false);
          v.setDeletedAt(null);
          v.setDeletedBy(null);
          v.setStatus(newVariantStatus);
        }

        case InventoryEventType.UPDATE_PRODUCT -> {
          v.setStatus(newVariantStatus);
        }
      }

      v.setStatusChangeReason(reason);

      ProductVariantInventoryStatus inventoryStatus =
          ProductStatusPolicy.mapInventoryStatus(productStatus);
      if (v.getInventory() != null) {
        v.getInventory().setStatus(inventoryStatus);
      }
    }

    productVariantRepository.saveAll(variants);
  }
}
