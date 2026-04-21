
package com.teamsolution.common.kafka.event.order;

import com.teamsolution.common.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderPaymentTimeoutEvent extends BaseEvent {

    private UUID orderId;
    private String orderNumber;
    private UUID customerId;
    private List<OrderItemEvent> items;
}
