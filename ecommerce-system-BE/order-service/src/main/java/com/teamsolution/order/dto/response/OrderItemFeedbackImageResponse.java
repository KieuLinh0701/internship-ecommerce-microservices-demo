package com.teamsolution.order.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemFeedbackImageResponse {
  private UUID id;
  private String imageUrl;
  private Integer sortOrder = 0;
}
