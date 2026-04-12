package com.teamsolution.cart.entity;

import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.util.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @Column(name = "customer_id")
  private UUID customerId;

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

  @Builder.Default
  @OneToMany(mappedBy = "cart")
  private List<CartItem> cartItems = new ArrayList<>();
}
