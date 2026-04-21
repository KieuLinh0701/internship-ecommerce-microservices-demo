package com.teamsolution.order.dto.response;

import com.teamsolution.order.enums.CouponType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponSummaryResponse {
  private String code;
  private CouponType type;
  private String title;
  private Long minOrderValue;
  private Integer usageRate;
  private LocalDateTime endDate;
  private Long discountAmount;
}
