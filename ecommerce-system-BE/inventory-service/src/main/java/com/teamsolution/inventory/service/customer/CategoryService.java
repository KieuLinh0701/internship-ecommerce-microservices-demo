package com.teamsolution.inventory.service.customer;

import com.teamsolution.inventory.dto.response.CategoryDetailResponse;
import com.teamsolution.inventory.dto.response.CategorySummaryResponse;
import com.teamsolution.inventory.entity.Category;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

  // REST
  List<CategorySummaryResponse> getActiveRootCategories();

  CategoryDetailResponse getActiveCategoryById(UUID id);

  // INTERNAL
  Category getActiveById(UUID id);
}
