package com.teamsolution.order.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.order.enums.OrderItemFeedbackStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "order_item_feedback")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderItemFeedback extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "product_slug", nullable = false)
  private String productSlug;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(name = "variant_image_url")
  private String variantImageUrl;

  @Column(name = "rating", nullable = false)
  private Short rating;

  @Column(name = "comment")
  private String comment;

  @Column(name = "is_anonymous", nullable = false)
  @Builder.Default
  private Boolean isAnonymous = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private OrderItemFeedbackStatus status = OrderItemFeedbackStatus.PENDING;

  @Column(name = "approved_At")
  private LocalDateTime approvedAt;

  @Column(name = "approved_By")
  private LocalDateTime approvedBy;

  @Column(name = "is_updated")
  @Builder.Default
  private Boolean isUpdated = false;

  @ToString.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL)
  private List<OrderItemFeedbackImage> images = new ArrayList<>();
}
