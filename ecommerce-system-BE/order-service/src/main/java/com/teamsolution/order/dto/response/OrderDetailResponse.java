package com.teamsolution.order.dto.response;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import com.teamsolution.common.core.enums.order.OrderStatus;
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
public class OrderDetailResponse {
  private UUID id;
  private String orderNumber;
  private OrderStatus status;
  private Long subtotal;
  private Long discountAmount;
  private Long shippingFee;
  private Long total;
  private OrderPaymentMethod paymentMethod;
  private String notes;
  private AddressResponse address;
  private List<OrderItemResponse> orderItems;
  private String cancelReason;
  private LocalDateTime cancelledAt;
  private LocalDateTime cancelAcceptedAt;
  private LocalDateTime createdAt;
  private LocalDateTime completedAt;
  private LocalDateTime paidAt;
  private LocalDateTime refundAt;
  private Boolean isFeedback;
  private Boolean canFeedback;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AddressResponse {
    private UUID id;
    private String name;
    private String phone;
    private String cityName;
    private String wardName;
    private String detail;
  }
}
