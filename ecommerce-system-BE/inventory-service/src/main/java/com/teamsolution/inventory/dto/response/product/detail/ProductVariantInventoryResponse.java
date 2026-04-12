package com.teamsolution.inventory.dto.response.product.detail;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantInventoryResponse {
  private UUID id;
  private UUID variantId;
  private Integer availableQuantity;
  private Integer soldQuantity;
}
