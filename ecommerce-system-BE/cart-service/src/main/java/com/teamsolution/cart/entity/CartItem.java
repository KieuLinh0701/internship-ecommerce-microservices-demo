package com.teamsolution.cart.entity;

import com.teamsolution.cart.enums.CartItemStatus;
import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.util.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id")
  private Cart cart;

  @Column(name = "product_id")
  private UUID productId;

  @Column(name = "variant_id")
  private UUID variantId;

  @Column(name = "quantity")
  private int quantity;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private CartItemStatus status = CartItemStatus.ACTIVE;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = false;

  @Enumerated(EnumType.STRING)
  private StatusChangeReason statusChangeReason;

  @Version
  @Column(name = "version")
  private Long version;
}
