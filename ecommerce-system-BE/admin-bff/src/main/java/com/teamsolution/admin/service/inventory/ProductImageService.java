package com.teamsolution.admin.service.inventory;

import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import java.util.List;
import java.util.UUID;

public interface ProductImageService {

  List<ProductImageResponse> createProductImage(CreateProductImageRequest request);

  void deleteProductImageById(UUID productImageId);
}
