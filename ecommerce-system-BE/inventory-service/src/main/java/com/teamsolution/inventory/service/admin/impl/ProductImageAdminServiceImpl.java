package com.teamsolution.inventory.service.admin.impl;

import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.inventory.config.properties.ProductImageProperties;
import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.enums.CloudinaryFolderType;
import com.teamsolution.inventory.enums.ProductImageStatus;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.mapper.admin.ProductImageAdminMapper;
import com.teamsolution.inventory.repository.ProductImageRepository;
import com.teamsolution.inventory.service.admin.ProductImageAdminService;
import com.teamsolution.inventory.storage.CloudinaryFolderResolver;
import com.teamsolution.inventory.storage.CloudinaryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductImageAdminServiceImpl implements ProductImageAdminService {

  private final ProductImageRepository productImageRepository;
  private final ProductImageAdminMapper productImageAdminMapper;
  private final CloudinaryService cloudinaryService;
  private final CloudinaryFolderResolver folderResolver;
  private final ProductImageProperties productImageProperties;

  private final Executor uploadExecutor;

  @Override
  @Transactional
  public List<ProductImageResponse> uploadTempImage(CreateProductImageRequest request) {

    if (request.files().size() > productImageProperties.getMaxFiles()) {
      throw new AppException(ErrorCode.PRODUCT_IMAGE_MAX_FILES_EXCEEDED);
    }

    String folder = folderResolver.getFolder(CloudinaryFolderType.PRODUCT);

    List<CompletableFuture<ProductImage>> futures =
        request.files().stream()
            .map(
                file ->
                    CompletableFuture.supplyAsync(() -> uploadSingle(file, folder), uploadExecutor))
            .toList();

    List<ProductImage> images = futures.stream().map(CompletableFuture::join).toList();

    return productImageRepository.saveAll(images).stream()
        .map(productImageAdminMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void deleteImage(UUID imageId, UUID adminAccountId) {
    ProductImage image =
        productImageRepository
            .findById(imageId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));

    if (Boolean.TRUE.equals(image.getIsDeleted())
        || image.getStatus() == ProductImageStatus.DELETED) {
      throw new AppException(ErrorCode.PRODUCT_IMAGE_ALREADY_DELETED);
    }

    if (image.getStatus() == ProductImageStatus.TEMP) {
      cloudinaryService.deleteImage(image.getPublicId());
      productImageRepository.delete(image);

      return;
    }

    image.setStatus(ProductImageStatus.DELETED);
    image.setDeletedAt(LocalDateTime.now());
    image.setDeletedBy(adminAccountId);
    image.setStatusChangeReason(StatusChangeReason.MANUAL);
    image.setIsDeleted(true);

    productImageRepository.save(image);
  }

  private ProductImage uploadSingle(MultipartFile file, String folder) {
    try {
      Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, folder);

      return ProductImage.builder()
          .imageUrl(uploadResult.get("url").toString())
          .publicId(uploadResult.get("publicId").toString())
          .status(ProductImageStatus.TEMP)
          .build();

    } catch (Exception e) {
      throw new AppException(ErrorCode.PRODUCT_IMAGE_UPLOAD_FAILED, e, file.getOriginalFilename());
    }
  }
}
