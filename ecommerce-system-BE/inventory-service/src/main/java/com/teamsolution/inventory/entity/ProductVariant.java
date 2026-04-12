package com.teamsolution.inventory.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.inventory.enums.ProductVariantStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "product_variants")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ProductVariant extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "sku")
  private String sku;

  @Column(name = "price")
  private Long price;

  @Column(name = "compare_at_price")
  private Long compareAtPrice;

  @Column(name = "image_url")
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private ProductVariantStatus status = ProductVariantStatus.DRAFT;

  @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL)
  private ProductVariantInventory inventory;

  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductVariantAttributeValue> attributeValues;
}
