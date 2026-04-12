package com.teamsolution.inventory.controller.internal;

import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.common.jpa.utils.PageableUtils;
import com.teamsolution.inventory.service.admin.ProductAdminService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class ProductInternalController {

  private final ProductAdminService productAdminService;

  //    @GetMapping("/stats")
  //    public ResponseEntity<ApiResponse<Map<FailedEventStatus, Long>>> getStats() {
  //
  //        Map<FailedEventStatus, Long> stats = failedEventService.getStats();
  //        return ResponseEntity.ok(ApiResponse.success(stats));
  //    }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getAll(
      @ModelAttribute FilterProductRequest filterRequest) {

    Pageable pageable =
        PageableUtils.toPageable(
            filterRequest.getPage(),
            filterRequest.getSize(),
            filterRequest.getSortBy(),
            filterRequest.getDirection(),
            false);

    Page<ProductSummaryResponse> failedEventDtos =
        productAdminService.getAll(filterRequest, pageable);
    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(failedEventDtos)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ProductSummaryResponse>> create(
      @Valid @RequestBody CreateProductRequest request) {
    return ResponseEntity.ok(ApiResponse.success(productAdminService.create(request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    UUID currentAdminAccountId = SecurityUtils.getCurrentAccountId();

    productAdminService.delete(id, currentAdminAccountId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<ApiResponse<Void>> restore(@PathVariable UUID id) {
    UUID currentAdminAccountId = SecurityUtils.getCurrentAccountId();

    productAdminService.restore(id, currentAdminAccountId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
