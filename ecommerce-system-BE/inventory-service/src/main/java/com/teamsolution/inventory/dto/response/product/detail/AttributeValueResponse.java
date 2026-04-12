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
public class AttributeValueResponse {
  private UUID id;
  private String value;
}
