package com.teamsolution.admin.controller.inventory;

import com.teamsolution.admin.service.inventory.ProductImageService;
import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {

  private final ProductImageService service;

  @PostMapping
  public ResponseEntity<ApiResponse<List<ProductImageResponse>>> create(
      @Valid @ModelAttribute CreateProductImageRequest request) {
    return ResponseEntity.ok(ApiResponse.success(service.createProductImage(request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
    service.deleteProductImageById(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
