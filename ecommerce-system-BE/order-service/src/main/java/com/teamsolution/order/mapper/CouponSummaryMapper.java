package com.teamsolution.order.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.order.dto.response.CouponSummaryResponse;
import com.teamsolution.order.entity.Coupon;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponSummaryMapper extends BaseMapper<Coupon, CouponSummaryResponse> {

  @Mapping(target = "usageRate", ignore = true)
  @Mapping(target = "discountAmount", ignore = true)
  CouponSummaryResponse toDto(Coupon coupon);

  @AfterMapping
  default void calculateUsageRate(Coupon coupon, @MappingTarget CouponSummaryResponse dto) {
    if (coupon.getUsageLimit() != null && coupon.getUsageLimit() > 0) {
      dto.setUsageRate((coupon.getUsedCount() * 100) / coupon.getUsageLimit());
    }
  }
}
