package com.teamsolution.inventory.service.internal.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.event.order.OrderItemEvent;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.kafka.producer.InventoryProducer;
import com.teamsolution.inventory.repository.ProductVariantInventoryRepository;
import com.teamsolution.inventory.service.internal.ProductVariantInventoryInternalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductVariantInventoryInternalServiceImpl
        implements ProductVariantInventoryInternalService {

  private final ProductVariantInventoryRepository inventoryRepository;
  private final InventoryProducer inventoryProducer;

    @Override
    @Transactional
    public void reserveStockForCreateOrder(OrderCreatedEvent event) {
        for (OrderItemEvent item : event.getItems()) {
            int updated = inventoryRepository.reserveStockIfSufficient(item.variantId(), item.quantity());

            if (updated == 0) {
                log.error("Reserve stock failed — variantId={}, quantity={}",
                        item.variantId(), item.quantity());

                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                inventoryProducer.publishInventoryReservationFailedEvent(
                        event.getAccountId(),
                        event.getAccountRoleId(),
                        event.getCustomerId(),
                        event.getEmail(),
                        event.getOrderId(),
                        event.getOrderNumber(),
                        List.of(NotificationChannel.WEB),
                        event.getCartItemIds()
                );

                return;
            }

            log.info("Reserved stock for variantId={}, quantity={}", item.variantId(), item.quantity());
        }
    }

    @Override
    @Transactional
    public void reserveStock(List<OrderItemEvent> items) {
        for (OrderItemEvent item : items) {
            int updated = inventoryRepository.reserveStockIfSufficient(item.variantId(), item.quantity());
            if (updated == 0) {
                log.error("Reserve stock failed — variantId={}, quantity={}",
                        item.variantId(), item.quantity());

                throw new PermanentException(
                        ErrorCode.INSUFFICIENT_STOCK,
                        item.variantId()
                );
            }

            log.info("Reserved stock for variantId={}, quantity={}", item.variantId(), item.quantity());
        }
    }

    @Override
    @Transactional
    public void confirmStockForItems(List<OrderItemEvent> items) {
        for (OrderItemEvent item : items) {
            int updated = inventoryRepository.confirmStock(item.variantId(), item.quantity());
            if (updated == 0) {
                throw new PermanentException(
                        ErrorCode.CONFIRM_STOCK_FAILED,
                        item.variantId()
                );
            }
        }
    }

    @Transactional
    public void restoreConfirmedStock(List<OrderItemEvent> items) {
        items.forEach(item -> {
            int updated = inventoryRepository
                    .restoreConfirmedStock(item.variantId(), item.quantity());

            if (updated == 0) {
                throw new PermanentException(
                        ErrorCode.RESTORE_CONFIRMED_STOCK_FAILED,
                        item.variantId()
                );
            }
        });
    }
}
