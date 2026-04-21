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
public class OrderItemResponse {
  private UUID id;
  private Product product;
  private Variant variant;
  private Integer quantity;
  private Long subtotal;
  private Long finalAmount;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Product {
    private UUID id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Variant {
    private String imageUrl;
    private String name;
  }
}
