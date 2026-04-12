package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.Category;
import com.teamsolution.inventory.enums.CategoryStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends BaseRepository<Category, UUID> {

  List<Category> findCategoriesByParentIdIsNullAndIsDeletedFalseAndStatus(CategoryStatus status);

  Optional<Category> findByIsDeletedFalseAndIdAndStatus(UUID id, CategoryStatus status);
}
