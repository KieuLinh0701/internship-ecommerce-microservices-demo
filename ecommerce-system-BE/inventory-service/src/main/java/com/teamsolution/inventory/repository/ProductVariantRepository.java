package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.ProductVariant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductVariantRepository
    extends BaseRepository<ProductVariant, UUID>, JpaSpecificationExecutor<ProductVariant> {

  @Query(
      """
            SELECT v FROM ProductVariant v
            JOIN FETCH v.attributeValues av
            JOIN FETCH av.attributeValue a
            JOIN FETCH a.attribute
            WHERE v.product.id = :productId
            AND v.isDeleted = false
            """)
  List<ProductVariant> findVariantsByProductId(UUID productId);

  boolean existsBySku(String sku);
}
