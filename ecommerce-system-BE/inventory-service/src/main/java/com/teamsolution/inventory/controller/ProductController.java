package com.teamsolution.inventory.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.inventory.dto.response.product.detail.ProductDetailResponse;
import com.teamsolution.inventory.service.customer.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  //  @GetMapping("/{productId}/variants/{variantId}")
  //  public ResponseEntity<ApiResponse<ProductVariantDto>> getVariantById(
  //      @PathVariable UUID productId, @PathVariable UUID variantId) {
  //
  //    ProductVariantDto variant = productService.getVariantById(productId, variantId);
  //    return ResponseEntity.ok(ApiResponse.success(variant));
  //  }
}
