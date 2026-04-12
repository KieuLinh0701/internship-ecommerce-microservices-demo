package com.teamsolution.inventory.service.internal.impl;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.inventory.dto.response.product.detail.ProductImageResponse;
import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.enums.ProductImageStatus;
import com.teamsolution.inventory.mapper.ProductImageMapper;
import com.teamsolution.inventory.repository.ProductImageRepository;
import com.teamsolution.inventory.service.internal.ProductImageInternalService;
import com.teamsolution.inventory.specification.ProductImageSpecification;
import com.teamsolution.inventory.storage.CloudinaryService;
import com.teamsolution.inventory.utils.ProductStatusPolicy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceInternalImpl implements ProductImageInternalService {

  private final ProductImageRepository productImageRepository;
  private final ProductImageMapper productImageMapper;
  private final CloudinaryService cloudinaryService;

  @Override
  public List<ProductImageResponse> getByProductId(UUID productId) {
    return productImageMapper.toDtoList(
        productImageRepository.findImagesByProductId(
            productId, List.of(ProductImageStatus.ACTIVE)));
  }

  @Override
  public List<ProductImage> saveAll(List<ProductImage> images) {
    return productImageRepository.saveAll(images);
  }

  @Override
  public List<ProductImage> findValidTempImages(List<UUID> ids) {
    return productImageRepository.findValidTempImages(ids, List.of(ProductImageStatus.TEMP));
  }

  @Override
  public List<ProductImage> findValidImagesByProductIds(List<UUID> ids) {
    return productImageRepository.findValidImagesByProductIds(
        ids, List.of(ProductImageStatus.ACTIVE));
  }

  @Override
  public void updateStatus(
      UUID productId,
      List<ProductImageStatus> requestStatuses,
      List<StatusChangeReason> requestReasons,
      ProductStatus productStatus,
      StatusChangeReason reason,
      UUID adminId,
      InventoryEventType eventType) {

    Specification<ProductImage> spec =
        Specification.where(ProductImageSpecification.hasProductId(productId))
            .and(ProductImageSpecification.inReasons(requestReasons))
            .and(ProductImageSpecification.inStatuses(requestStatuses));

    List<ProductImage> images = productImageRepository.findAll(spec);

    ProductImageStatus newStatus = ProductStatusPolicy.mapImageStatus(productStatus);

    for (ProductImage v : images) {

      switch (eventType) {
        case InventoryEventType.DELETE_PRODUCT -> {
          v.setIsDeleted(true);
          v.setDeletedAt(LocalDateTime.now());
          v.setDeletedBy(adminId);
          v.setStatus(newStatus);
        }

        case InventoryEventType.RESTORE_PRODUCT -> {
          v.setIsDeleted(false);
          v.setDeletedAt(null);
          v.setDeletedBy(null);
          v.setStatus(newStatus);
        }

        case InventoryEventType.UPDATE_PRODUCT -> {
          v.setStatus(newStatus);
        }
      }

      v.setStatusChangeReason(reason);
    }

    productImageRepository.saveAll(images);
  }

  @Transactional
  public int cleanupTempImages(long olderThanMinutes) {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(olderThanMinutes);

    List<ProductImage> staleImages =
        productImageRepository.findByStatusAndCreatedAtBefore(ProductImageStatus.TEMP, cutoff);

    if (staleImages.isEmpty()) {
      log.info("No stale TEMP images found before {}", cutoff);
      return 0;
    }

    List<String> publicIds = staleImages.stream().map(ProductImage::getPublicId).toList();

    cloudinaryService.deleteImages(publicIds);

    List<UUID> ids = staleImages.stream().map(ProductImage::getId).toList();
    productImageRepository.deleteAllByIds(ids);

    log.info(
        "Cleaned up {} stale TEMP images (older than {} minutes)",
        staleImages.size(),
        olderThanMinutes);
    return staleImages.size();
  }
}
