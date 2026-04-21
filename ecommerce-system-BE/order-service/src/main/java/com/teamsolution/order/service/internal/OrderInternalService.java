package com.teamsolution.order.service.internal;

import com.teamsolution.order.entity.Order;
import java.util.List;
import java.util.UUID;

public interface OrderInternalService {

  Order findByIdAndCustomerId(UUID orderId, UUID customerId);

  void handleInventoryFailed(UUID orderId, UUID accountId, UUID accountRoleId, UUID customerId);

  void handleRefundCompleted(UUID orderId, UUID customerId);

  void handleRefundFailed(UUID orderId, UUID customerId);

  void handlePaymentCompleted(UUID orderId, UUID customerId);

  void handlePaymentFailed(UUID orderId, UUID customerId);

  List<Order> bulkCancelExpiredUnpaidOrders();
}
