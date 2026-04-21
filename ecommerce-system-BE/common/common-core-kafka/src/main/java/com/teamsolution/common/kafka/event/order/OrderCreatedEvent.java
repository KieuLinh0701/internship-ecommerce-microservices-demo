package com.teamsolution.common.kafka.event.order;

import com.teamsolution.common.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
public class OrderCreatedEvent
        extends BaseEvent {

    private UUID accountRoleId;
    private UUID accountId;
    private UUID customerId;
    private String email;
    private UUID orderId;
    private String orderNumber;
    private List<OrderItemEvent> items;
    private List<UUID> cartItemIds;
}
