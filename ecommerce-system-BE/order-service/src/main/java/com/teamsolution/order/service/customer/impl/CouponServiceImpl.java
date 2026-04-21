package com.teamsolution.order.service.customer.impl;

import com.teamsolution.order.dto.request.AvailableCouponRequest;
import com.teamsolution.order.dto.response.CouponSummaryResponse;
import com.teamsolution.order.entity.Coupon;
import com.teamsolution.order.mapper.CouponSummaryMapper;
import com.teamsolution.order.repository.CouponRepository;
import com.teamsolution.order.service.customer.CouponService;
import com.teamsolution.order.service.internal.CouponInternalService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

  private final CouponRepository couponRepository;
  private final CouponSummaryMapper couponSummaryMapper;

  private final CouponInternalService couponInternalService;

  @Override
  public List<CouponSummaryResponse> getAvailableCoupons(AvailableCouponRequest request) {
    if (request.couponId() != null) {
      Coupon coupon =
          couponInternalService.validateAndGetCoupon(
              request.couponId(), request.orderValue(), request.shippingFee());
      return List.of(toCouponSummaryResponse(coupon, request.orderValue(), request.shippingFee()));
    }

    return couponRepository
        .findAvailableCoupons(request.orderValue(), request.shippingFee(), LocalDateTime.now())
        .stream()
        .map(coupon -> toCouponSummaryResponse(coupon, request.orderValue(), request.shippingFee()))
        .toList();
  }

  private CouponSummaryResponse toCouponSummaryResponse(
      Coupon coupon, Long orderValue, Long shippingFee) {
    CouponSummaryResponse dto = couponSummaryMapper.toDto(coupon);
    dto.setDiscountAmount(couponInternalService.calculateDiscount(coupon, orderValue, shippingFee));
    return dto;
  }
}
