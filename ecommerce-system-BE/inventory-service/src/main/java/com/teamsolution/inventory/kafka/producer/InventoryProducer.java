package com.teamsolution.inventory.kafka.producer;

import com.teamsolution.common.core.enums.notification.NotificationChannel;

import java.util.List;
import java.util.UUID;

public interface InventoryProducer {
  void publishInventoryReservationFailedEvent(
          UUID accountId,
          UUID accountRoleId,
          UUID customerId,
          String email,
          UUID orderId,
          String orderNumber,
          List<NotificationChannel> channelList,
          List<UUID> cartItemIds
  );
}
