package com.teamsolution.common.kafka.event.order;

import com.teamsolution.common.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderRefundRequestedEvent
        extends BaseEvent {

  private UUID accountRoleId;
  private UUID accountId;
  private String email;
  private UUID customerId;
  private UUID orderId;
  private String orderNumber;
}
