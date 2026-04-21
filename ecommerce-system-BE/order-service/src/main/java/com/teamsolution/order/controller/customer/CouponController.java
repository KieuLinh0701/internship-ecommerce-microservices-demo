package com.teamsolution.order.controller.customer;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.order.dto.request.AvailableCouponRequest;
import com.teamsolution.order.dto.response.CouponSummaryResponse;
import com.teamsolution.order.service.customer.CouponService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders/coupons")
public class CouponController {

  private final CouponService couponService;

  @GetMapping("/available")
  public ResponseEntity<ApiResponse<List<CouponSummaryResponse>>> getAvailableCoupons(
      @Valid @ModelAttribute AvailableCouponRequest request) {
    return ResponseEntity.ok(ApiResponse.success(couponService.getAvailableCoupons(request)));
  }
}
