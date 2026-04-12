package com.teamsolution.inventory.service.admin;

import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import java.util.List;
import java.util.UUID;

public interface ProductImageAdminService {
  List<ProductImageResponse> uploadTempImage(CreateProductImageRequest request);

  void deleteImage(UUID imageId, UUID adminAccountId);
}
