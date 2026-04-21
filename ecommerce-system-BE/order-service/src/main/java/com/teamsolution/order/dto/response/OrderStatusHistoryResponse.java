package com.teamsolution.order.dto.response;

import com.teamsolution.common.core.enums.order.OrderStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryResponse {
  private OrderStatus status;
  private LocalDateTime time;
}
