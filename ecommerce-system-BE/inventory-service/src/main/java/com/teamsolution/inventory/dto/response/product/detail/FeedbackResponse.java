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
public class FeedbackResponse {
  private UUID id;
  private UUID customerId;
  private String customerName;
  private Short rating;
  private String comment;
}
