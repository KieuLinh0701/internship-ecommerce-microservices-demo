package com.teamsolution.common.core.dto.admin.inventory.product.request;

import com.teamsolution.common.core.dto.common.request.BaseFilterRequest;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterProductRequest extends BaseFilterRequest {
  private String keyword;
  private UUID categoryId;
  private UUID brandId;
  private ProductStatus status;
  private Long minPrice;
  private Long maxPrice;
  private Boolean isDeleted = false;
}
