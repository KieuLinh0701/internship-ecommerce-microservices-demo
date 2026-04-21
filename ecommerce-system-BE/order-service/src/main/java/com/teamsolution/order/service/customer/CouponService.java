package com.teamsolution.order.service.customer;

import com.teamsolution.order.dto.request.AvailableCouponRequest;
import com.teamsolution.order.dto.response.CouponSummaryResponse;
import java.util.List;

public interface CouponService {

  List<CouponSummaryResponse> getAvailableCoupons(AvailableCouponRequest request);
}
