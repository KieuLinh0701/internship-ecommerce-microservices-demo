package com.teamsolution.inventory.service.customer;

import com.teamsolution.inventory.dto.response.product.detail.ProductDetailResponse;
import java.util.UUID;

public interface ProductService {
  ProductDetailResponse getProductById(UUID id);

  //  ProductVariantDto getVariantById(UUID productId, UUID variantId);
}
