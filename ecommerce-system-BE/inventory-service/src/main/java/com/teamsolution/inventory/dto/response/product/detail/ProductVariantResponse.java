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
public class ProductVariantResponse {
  private UUID id;
  private Long price;
  private String imageUrl;
  private ProductVariantInventoryResponse inventory;
}
