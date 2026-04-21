package com.teamsolution.common.kafka.event.order;

import com.teamsolution.common.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.teamsolution.common.core.enums.order.OrderStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderCancelledEvent extends BaseEvent {

  private UUID accountId;
  private UUID accountRoleId;
  private UUID customerId;
  private UUID orderId;
  private String email;
  private String orderNumber;
  private String previousStatus;
  private boolean needRefund;
  private List<OrderItemEvent> items;

  public boolean isPreviousStatus(OrderStatus status) {
    return status.name().equals(this.previousStatus);
  }
}
