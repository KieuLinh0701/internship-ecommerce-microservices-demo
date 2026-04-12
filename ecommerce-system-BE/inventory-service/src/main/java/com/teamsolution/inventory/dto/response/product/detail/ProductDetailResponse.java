package com.teamsolution.inventory.dto.response.product.detail;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
  private UUID id;
  private String name;
  private String slug;
  private String description;
  private Long minPrice;
  private Long maxPrice;
  private ProductStatus status;
  private CategoryResponse category;
  private BrandResponse brand;
  private List<ProductImageResponse> images;
  private List<AttributeWithValuesResponse> attributes;
  private List<ProductVariantResponse> variants;
}
