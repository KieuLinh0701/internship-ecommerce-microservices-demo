package com.teamsolution.inventory.entity;

import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.inventory.enums.ProductVariantInventoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "product_variant_inventory")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class ProductVariantInventory {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id")
  private ProductVariant variant;

  @Column(name = "quantity")
  @Builder.Default
  private Integer quantity = 0;

  @Column(name = "reserved_quantity")
  @Builder.Default
  private Integer reservedQuantity = 0;

  @Column(name = "low_stock_threshold")
  @Builder.Default
  private Integer lowStockThreshold = 10;

  @Column(name = "manufacture_date")
  private LocalDate manufactureDate;

  @Column(name = "expiry_date")
  private LocalDate expiryDate;

  @Column(name = "sold_quantity")
  @Builder.Default
  private Integer soldQuantity = 0;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ProductVariantInventoryStatus status = ProductVariantInventoryStatus.FROZEN;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private UUID createdBy;

  @LastModifiedBy
  @Column(name = "updated_by")
  private UUID updatedBy;

  @Version
  @Column(name = "version")
  private Long version;

  public Integer getAvailableQuantity() {
    return quantity - reservedQuantity;
  }

  public boolean isExpired() {
    if (expiryDate == null) {
      return false;
    }
    return LocalDate.now().isAfter(expiryDate);
  }

  public long daysUntilExpiry() {
    if (expiryDate == null) {
      return -1;
    }
    return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
  }
}
