package com.teamsolution.order.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.order.enums.CouponType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_coupon")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Coupon extends BaseEntity {

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private CouponType type;

  @Column(name = "value", nullable = false)
  private BigDecimal value;

  @Column(name = "min_order_value")
  @Builder.Default
  private Long minOrderValue = 0L;

  @Column(name = "max_discount")
  private Long maxDiscount;

  @Column(name = "usage_limit")
  private Integer usageLimit;

  @Column(name = "used_count", nullable = false)
  @Builder.Default
  private Integer usedCount = 0;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "description")
  private String description;

  @Column(name = "title")
  private String title;

  public boolean isActive() {
    return Boolean.TRUE.equals(isActive);
  }

  public boolean isWithinDateRange() {
    LocalDateTime now = LocalDateTime.now();
    return !now.isBefore(startDate) && !now.isAfter(endDate);
  }

  public boolean isLimitReached() {
    return usageLimit != null && usedCount >= usageLimit;
  }

  public boolean meetsMinOrderValue(Long orderValue, Long shippingFee) {
    return switch (type) {
      case FREE_SHIP -> shippingFee >= minOrderValue;
      default -> orderValue >= minOrderValue;
    };
  }
}
