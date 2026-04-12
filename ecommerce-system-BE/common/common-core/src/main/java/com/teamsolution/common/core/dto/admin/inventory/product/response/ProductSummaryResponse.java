package com.teamsolution.common.core.dto.admin.inventory.product.response;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ProductSummaryResponse {
  private UUID id;
  private String name;
  private String slug;
  private Long minPrice;
  private Long maxPrice;
  private ProductStatus status;
  private String thumbnailImage;

  //    soldCount
  //    rating
  //    reviewCount
  //    isFeatured
}
