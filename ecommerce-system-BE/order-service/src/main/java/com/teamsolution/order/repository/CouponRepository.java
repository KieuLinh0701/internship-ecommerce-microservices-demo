package com.teamsolution.order.repository;

import com.teamsolution.common.jpa.repository.BaseSoftDeleteRepository;
import com.teamsolution.order.entity.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends BaseSoftDeleteRepository<Coupon, UUID> {
  @Query(
      """
                SELECT c FROM Coupon c
                WHERE c.isDeleted = false
                AND c.isActive = true
                AND c.startDate <= :now
                AND c.endDate >= :now
                AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)
                AND (
                    (c.type != 'FREE_SHIP' AND c.minOrderValue <= :orderValue)
                    OR
                    (c.type = 'FREE_SHIP' AND c.minOrderValue <= :shippingFee)
                )
                ORDER BY c.endDate ASC
            """)
  List<Coupon> findAvailableCoupons(
      @Param("orderValue") Long orderValue,
      @Param("shippingFee") Long shippingFee,
      @Param("now") LocalDateTime now);
}
