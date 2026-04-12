package com.teamsolution.inventory.controller.internal;

import com.teamsolution.common.core.dto.admin.inventory.productimage.request.CreateProductImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.inventory.service.admin.ProductImageAdminService;
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
@RequestMapping("/internal/product-images")
@RequiredArgsConstructor
public class ProductImageInternalController {

  private final ProductImageAdminService productImageAdminService;

  @PostMapping
  public ResponseEntity<ApiResponse<List<ProductImageResponse>>> create(
      @Valid @ModelAttribute CreateProductImageRequest request) {
    return ResponseEntity.ok(
        ApiResponse.success(productImageAdminService.uploadTempImage(request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    UUID currentAdminAccountId = SecurityUtils.getCurrentAccountId();

    productImageAdminService.deleteImage(id, currentAdminAccountId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
