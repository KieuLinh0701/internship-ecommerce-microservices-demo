package com.teamsolution.inventory.repository;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository
    extends BaseRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

  boolean existsBySlug(String slug);

  List<Product> findByIsDeletedFalseAndStatus(ProductStatus status);

  @Query(
      """
            SELECT p FROM Product p
            LEFT JOIN FETCH p.brand
            LEFT JOIN FETCH p.category
            WHERE p.id = :id
            AND p.status IN :statuses
            """)
  Optional<Product> findDetailProduct(UUID id, List<ProductStatus> statuses);
}
