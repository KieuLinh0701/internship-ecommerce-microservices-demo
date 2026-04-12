package com.teamsolution.inventory.service.customer;

import com.teamsolution.inventory.dto.request.MatchVariantRequest;
import com.teamsolution.inventory.dto.response.product.detail.ProductDetailResponse;
import com.teamsolution.inventory.dto.response.product.detail.ProductVariantResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
  ProductDetailResponse getProductById(UUID id);

    List<ProductVariantResponse> getVariantsByProductId(UUID productId);

    ProductVariantResponse getVariantByAttributeValues(UUID productId, MatchVariantRequest request);
}
