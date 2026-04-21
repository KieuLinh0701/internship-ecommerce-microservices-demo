package com.teamsolution.order.service.internal;

import com.teamsolution.order.entity.Coupon;
import com.teamsolution.order.entity.OrderCoupon;
import java.util.List;
import java.util.UUID;

public interface CouponInternalService {

  Coupon validateAndGetCoupon(UUID id, Long orderValue, Long shippingFee);

  long calculateDiscount(Coupon coupon, Long orderValue, Long shippingFee);

  void incrementUsedCount(UUID couponId);

  void restoreCouponUsageIfNeeded(List<OrderCoupon> orderCoupons);
}
