package com.teamsolution.common.kafka.event.inventory;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
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
public class InventoryReservationFailedEvent extends BaseEvent {
    private UUID accountRoleId;
    private UUID accountId;
    private UUID customerId;
    private String email;
    private UUID orderId;
    private String orderNumber;
    private List<NotificationChannel> channels;
    private List<UUID> cartItemIds;
}
