package com.teamsolution.inventory.service.customer.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.inventory.dto.response.CategoryDetailResponse;
import com.teamsolution.inventory.dto.response.CategorySummaryResponse;
import com.teamsolution.inventory.entity.Category;
import com.teamsolution.inventory.enums.CategoryStatus;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.mapper.CategorySummaryMapper;
import com.teamsolution.inventory.repository.CategoryRepository;
import com.teamsolution.inventory.service.customer.CategoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategorySummaryMapper categorySummaryMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CategorySummaryResponse> getActiveRootCategories() {
    List<Category> root =
        categoryRepository.findCategoriesByParentIdIsNullAndIsDeletedFalseAndStatus(
            CategoryStatus.ACTIVE);

    return categorySummaryMapper.toDtoList(root);
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryDetailResponse getActiveCategoryById(UUID id) {
    Category category = getActiveById(id);

    return mapToTree(category);
  }

  @Override
  @Transactional(readOnly = true)
  public Category getActiveById(UUID id) {
    return categoryRepository
        .findByIsDeletedFalseAndIdAndStatus(id, CategoryStatus.ACTIVE)
        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
  }

  private CategoryDetailResponse mapToTree(Category category) {
    List<CategoryDetailResponse> children =
        category.getChildren().stream()
            .filter(child -> !child.getIsDeleted())
            .map(this::mapToTree)
            .toList();

    return CategoryDetailResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .slug(category.getSlug())
        .children(children)
        .build();
  }
}
