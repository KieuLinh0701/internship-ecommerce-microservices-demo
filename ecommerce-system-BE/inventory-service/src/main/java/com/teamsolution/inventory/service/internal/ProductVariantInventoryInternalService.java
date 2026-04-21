package com.teamsolution.inventory.service.internal;

import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.event.order.OrderItemEvent;

import java.util.List;

public interface ProductVariantInventoryInternalService {

    void reserveStockForCreateOrder(OrderCreatedEvent event);

    void reserveStock(List<OrderItemEvent> items);

    void confirmStockForItems(List<OrderItemEvent> items);

    void restoreConfirmedStock(List<OrderItemEvent> items);
}
