package com.teamsolution.order.entity;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import com.teamsolution.common.core.enums.order.OrderPaymentStatus;
import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.order.enums.OrderCancelReason;
import com.teamsolution.order.enums.OrderReturnReason;
import com.teamsolution.order.utils.FeedbackPolicyUtils;
import com.teamsolution.order.utils.OrderNumberUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "orders")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "order_number")
  @Builder.Default
  private String orderNumber = OrderNumberUtils.generateOrderNumber();

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private OrderStatus status = OrderStatus.PENDING;

  @Column(name = "subtotal", nullable = false)
  private Long subtotal;

  @Column(name = "discount_amount", nullable = false)
  @Builder.Default
  private Long discountAmount = 0L;

  @Column(name = "discount_shipping_fee", nullable = false)
  @Builder.Default
  private Long discountShippingFee = 0L;

  @Column(name = "shipping_fee", nullable = false)
  @Builder.Default
  private Long shippingFee = 0L;

  @Column(name = "total", nullable = false)
  private Long total;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  private OrderPaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status")
  @Builder.Default
  private OrderPaymentStatus paymentStatus = OrderPaymentStatus.UNPAID;

  @Column(name = "address_id", nullable = false)
  private UUID addressId;

  @Column(name = "receiver_name", nullable = false)
  private String receiverName;

  @Column(name = "receiver_phone", nullable = false)
  private String receiverPhone;

  @Column(name = "city_code", nullable = false)
  private Integer cityCode;

  @Column(name = "city_name", nullable = false)
  private String cityName;

  @Column(name = "ward_code", nullable = false)
  private Integer wardCode;

  @Column(name = "ward_name", nullable = false)
  private String wardName;

  @Column(name = "address_detail", nullable = false)
  private String addressDetail;

  @Column(name = "notes")
  private String notes;

  @Enumerated(EnumType.STRING)
  @Column(name = "cancel_reason")
  private OrderCancelReason cancelReason;

  @Column(name = "cancel_note", length = 255)
  private String cancelNote;

  @Column(name = "cancelled_by")
  private UUID cancelledBy;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Column(name = "cancel_accepted_at")
  private LocalDateTime cancelAcceptedAt;

  @Column(name = "cancel_accepted_by")
  private UUID cancelAcceptedBy;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @Column(name = "refund_at")
  private LocalDateTime refundAt;

  @Column(name = "returned_at")
  private LocalDateTime returnedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "return_reason")
  private OrderReturnReason returnReason;

  @Column(name = "return_images")
  private List<String> returnImages;

  @Column(name = "is_feedback")
  @Builder.Default
  private Boolean isFeedback = false;

  @ToString.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderItem> orderItems = new ArrayList<>();

  @ToString.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderCoupon> orderCoupons = new ArrayList<>();

  @Transient
  public Boolean getCanFeedback() {
    if (Boolean.TRUE.equals(isFeedback)) return false;
    if (completedAt == null) return false;
    if (status != OrderStatus.COMPLETED) return false;

    return LocalDateTime.now().isBefore(completedAt.plusDays(FeedbackPolicyUtils.CREATE_DAYS));
  }
}
