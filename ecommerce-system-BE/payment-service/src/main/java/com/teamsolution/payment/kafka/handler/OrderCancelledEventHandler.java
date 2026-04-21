package com.teamsolution.payment.kafka.handler;

import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.payment.service.FailedEventSaverService;
import com.teamsolution.payment.service.PaymentRefundService;
import com.teamsolution.payment.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledEventHandler {

    private static final String EVENT = "OrderCancelledEvent";

    private final PaymentRefundService paymentRefundService;
    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;

    @Transactional
    public void handle(OrderCancelledEvent event) {
        if (!event.isNeedRefund()) {
            log.info("[Payment][{}] No refund required, skip processing, orderId={}",
                    EVENT, event.getOrderId());
            return;
        }

        log.info("[Payment][{}] Preparing refund request, orderId={}, accountId={}, customerId={}",
                EVENT, event.getOrderId(), event.getAccountId(), event.getCustomerId());

        paymentRefundService.refund(
                event.getOrderId(),
                event.getOrderNumber(),
                event.getCustomerId(),
                event.getEmail(),
                event.getAccountId(),
                event.getAccountRoleId()
        );

        processedEventService.markProcessed(event.getId());

        log.info("[Payment][{}] Refund request processed successfully, orderId={}",
                EVENT, event.getOrderId());
    }

    @Transactional
    public void handleRetry(OrderCancelledEvent event, UUID failedEventId) {
        log.info("[Retry][Payment][{}] Processing orderId={}", EVENT, event.getOrderId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info("[Retry][Payment][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }
}