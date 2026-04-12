package com.teamsolution.inventory.dto.response.product.detail;

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
public class AttributeWithValuesResponse {
  private UUID id;
  private String name;
  private List<AttributeValueResponse> values;
}
