package com.teamsolution.inventory.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.inventory.dto.response.CategoryDetailResponse;
import com.teamsolution.inventory.dto.response.CategorySummaryResponse;
import com.teamsolution.inventory.service.customer.CategoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> getActiveRootCategories() {
    List<CategorySummaryResponse> categoryResponses = categoryService.getActiveRootCategories();
    return ResponseEntity.ok(ApiResponse.success(categoryResponses));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryDetailResponse>> getActiveCategoryBySlug(
      @PathVariable UUID id) {
    CategoryDetailResponse categoryDto = categoryService.getActiveCategoryById(id);
    return ResponseEntity.ok(ApiResponse.success(categoryDto));
  }
}
