package com.teamsolution.order.service.internal.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.order.entity.Coupon;
import com.teamsolution.order.entity.OrderCoupon;
import com.teamsolution.order.exception.ErrorCode;
import com.teamsolution.order.repository.CouponRepository;
import com.teamsolution.order.service.internal.CouponInternalService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponInternalServiceImpl implements CouponInternalService {

  private final CouponRepository couponRepository;

  @Override
  @Transactional(readOnly = true)
  public Coupon validateAndGetCoupon(UUID id, Long orderValue, Long shippingFee) {
    Coupon coupon =
        couponRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

    if (!coupon.isActive()) throw new AppException(ErrorCode.COUPON_INACTIVE);
    if (!coupon.isWithinDateRange()) throw new AppException(ErrorCode.COUPON_EXPIRED);
    if (coupon.isLimitReached()) throw new AppException(ErrorCode.COUPON_LIMIT_REACHED);
    if (!coupon.meetsMinOrderValue(orderValue, shippingFee))
      throw new AppException(ErrorCode.COUPON_MIN_ORDER_NOT_MET);

    return coupon;
  }

  @Override
  @Transactional(readOnly = true)
  public long calculateDiscount(Coupon coupon, Long orderValue, Long shippingFee) {
    return switch (coupon.getType()) {
      case PERCENT -> {
        long discount = orderValue * coupon.getValue().longValue() / 100;
        yield coupon.getMaxDiscount() != null
            ? Math.min(discount, coupon.getMaxDiscount())
            : discount;
      }
      case FIXED -> Math.min(coupon.getValue().longValue(), orderValue);
      case FREE_SHIP ->
          coupon.getMaxDiscount() != null
              ? Math.min(shippingFee, coupon.getMaxDiscount())
              : shippingFee;
    };
  }

  @Override
  @Transactional
  public void incrementUsedCount(UUID couponId) {
    Coupon latestCoupon =
        couponRepository
            .findByIdAndIsDeletedFalse(couponId)
            .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

    if (latestCoupon.isLimitReached()) {
      throw new AppException(ErrorCode.COUPON_LIMIT_REACHED);
    }

    latestCoupon.setUsedCount(latestCoupon.getUsedCount() + 1);
    couponRepository.save(latestCoupon);
  }

  @Override
  @Transactional
  public void restoreCouponUsageIfNeeded(List<OrderCoupon> orderCoupons) {
    if (orderCoupons == null || orderCoupons.isEmpty()) return;

    List<Coupon> coupons = orderCoupons.stream().map(OrderCoupon::getCoupon).toList();

    coupons.forEach(coupon -> coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1)));
    couponRepository.saveAll(coupons);
  }
}
