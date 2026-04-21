package com.teamsolution.inventory.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.inventory.dto.request.MatchVariantRequest;
import com.teamsolution.inventory.dto.response.product.detail.AttributeWithValuesResponse;
import com.teamsolution.inventory.dto.response.product.detail.ProductDetailResponse;
import com.teamsolution.inventory.service.customer.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getBySlug(@PathVariable UUID id) {
        ProductDetailResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/{productId}/attributes")
    public ResponseEntity<ApiResponse<List<AttributeWithValuesResponse>>> getAttributes(
            @PathVariable UUID productId) {

        return ResponseEntity.ok(
                ApiResponse.success(productService.getAttributesByProductId(productId))
        );
    }

    @GetMapping("/{productId}/variants/match")
    public ResponseEntity<ApiResponse<UUID>> getVariantByAttributes(
            @PathVariable UUID productId,
            @RequestBody MatchVariantRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.getVariantByAttributeValues(productId, request)));
    }
}
