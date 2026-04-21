package com.teamsolution.order.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "order_items")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(name = "variant_id")
  private UUID variantId;

  @Column(name = "variant_name")
  private String variantName;

  @Column(name = "variant_image_url")
  private String variantImageUrl;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "unit_price", nullable = false)
  private Long unitPrice;

  @Column(name = "subtotal", nullable = false)
  private Long subtotal;

  @Column(name = "discount_amount", nullable = false)
  @Builder.Default
  private Long discountAmount = 0L;

  @Column(name = "final_amount", nullable = false)
  private Long finalAmount;

  @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private OrderItemFeedback feedback;
}
