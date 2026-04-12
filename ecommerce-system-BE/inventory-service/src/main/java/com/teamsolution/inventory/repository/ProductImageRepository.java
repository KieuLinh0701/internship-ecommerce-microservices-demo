package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.enums.ProductImageStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductImageRepository
    extends BaseRepository<ProductImage, UUID>, JpaSpecificationExecutor<ProductImage> {

  @Query(
      """
            SELECT i FROM ProductImage i
            WHERE i.product.id IN :productIds
            AND i.isDeleted = false
            AND i.status IN :statuses
            """)
  List<ProductImage> findValidImagesByProductIds(
      @Param("productIds") List<UUID> productIds,
      @Param("statuses") List<ProductImageStatus> statuses);

  @Query(
      """
            SELECT i FROM ProductImage i
            WHERE i.product.id = :productId
            AND i.isDeleted = false
            AND i.status IN :statuses
            ORDER BY i.sortOrder ASC
            """)
  List<ProductImage> findImagesByProductId(
      @Param("productId") UUID productId, @Param("statuses") List<ProductImageStatus> statuses);

  @Query(
      """
                SELECT i FROM ProductImage i
                WHERE i.id IN :ids
                  AND i.status in :statuses
                  AND i.product IS NULL
                  AND i.isDeleted = false
            """)
  List<ProductImage> findValidTempImages(
      @Param("ids") List<UUID> ids, @Param("statuses") List<ProductImageStatus> statuses);

  List<ProductImage> findByProductIdAndStatus(UUID productId, ProductImageStatus status);

  List<ProductImage> findByProductIdAndStatusIn(UUID productId, List<ProductImageStatus> statuses);

  @Query("SELECT p FROM ProductImage p WHERE p.status = :status AND p.createdAt < :before")
  List<ProductImage> findByStatusAndCreatedAtBefore(
      @Param("status") ProductImageStatus status, @Param("before") LocalDateTime before);

  @Modifying
  @Query("DELETE FROM ProductImage p WHERE p.id IN :ids")
  void deleteAllByIds(@Param("ids") List<UUID> ids);
}
