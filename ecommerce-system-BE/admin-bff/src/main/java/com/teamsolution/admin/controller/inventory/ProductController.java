package com.teamsolution.admin.controller.inventory;

import com.teamsolution.admin.service.inventory.ProductService;
import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.UpdateProductStatusRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductDetailResponse;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getAll(
      @ModelAttribute FilterProductRequest request) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProducts(request)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDetailResponse>> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ProductSummaryResponse>> create(
      @RequestBody CreateProductRequest request) {
    return ResponseEntity.ok(ApiResponse.success(productService.createProduct(request)));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDetailResponse>> updateById(
      @PathVariable UUID id, @RequestBody UpdateProductRequest request) {
    return ResponseEntity.ok(ApiResponse.success(productService.updateProductById(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
    productService.deleteProductById(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Void>> restoreById(@PathVariable UUID id) {
    productService.restoreProductById(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<ProductSummaryResponse>> updateStatusById(
      @PathVariable UUID id, @RequestBody UpdateProductStatusRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(productService.updateProductStatusById(id, request)));
  }
}
