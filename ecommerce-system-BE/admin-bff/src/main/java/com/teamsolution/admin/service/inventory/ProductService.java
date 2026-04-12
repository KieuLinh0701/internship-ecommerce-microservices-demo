package com.teamsolution.admin.service.inventory;

import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductStatusRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductDetailResponse;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;

public interface ProductService {
  PageResponse<ProductSummaryResponse> getProducts(FilterProductRequest filterRequest);

  ProductDetailResponse getProductById(UUID productId);

  ProductSummaryResponse createProduct(CreateProductRequest request);

  ProductDetailResponse updateProductById(UUID productId, UpdateProductRequest request);

  void deleteProductById(UUID productId);

  void restoreProductById(UUID productId);

  ProductSummaryResponse updateProductStatusById(
      UUID productId, UpdateProductStatusRequest request);
}
