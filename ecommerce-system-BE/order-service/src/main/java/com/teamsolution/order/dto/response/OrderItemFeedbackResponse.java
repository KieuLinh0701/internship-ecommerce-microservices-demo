package com.teamsolution.order.dto.response;

import com.teamsolution.order.enums.OrderItemFeedbackStatus;
import java.time.LocalDateTime;
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
public class OrderItemFeedbackResponse {
  private UUID id;
  private UUID productId;
  private String productSlug;
  private String productName;
  private String variantImageUrl;
  private Short rating;
  private String comment;
  private Boolean isUpdated;
  private List<OrderItemFeedbackImageResponse> images;
  private LocalDateTime createdAt;
  private String displayName;
  private OrderItemFeedbackStatus status;
}
