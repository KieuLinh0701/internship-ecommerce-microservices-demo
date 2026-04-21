package com.teamsolution.inventory.service.customer;

import com.teamsolution.inventory.dto.request.MatchVariantRequest;
import com.teamsolution.inventory.dto.response.product.detail.AttributeWithValuesResponse;
import com.teamsolution.inventory.dto.response.product.detail.ProductDetailResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDetailResponse getProductById(UUID id);

    List<AttributeWithValuesResponse> getAttributesByProductId(UUID productId);

    UUID getVariantByAttributeValues(UUID productId, MatchVariantRequest request);
}
