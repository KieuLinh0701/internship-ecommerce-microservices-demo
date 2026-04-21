package com.teamsolution.payment.kafka.handler;

import com.teamsolution.common.kafka.event.order.OrderRefundRequestedEvent;
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
public class OrderRefundRequestedEventHandler {

    private static final String EVENT = "OrderRefundRequestedEvent";

    private final PaymentRefundService paymentRefundService;
    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;

    @Transactional
    public void handle(OrderRefundRequestedEvent event) {
        log.info("[Payment][{}] Processing refund for orderId={}, orderNumber={}, accountId={}, accountRoleId={}",
                EVENT, event.getOrderId(), event.getOrderNumber(), event.getAccountId(), event.getAccountRoleId());

        paymentRefundService.refund(
                event.getOrderId(),
                event.getOrderNumber(),
                event.getCustomerId(),
                event.getEmail(),
                event.getAccountId(),
                event.getAccountRoleId()
        );

        processedEventService.markProcessed(event.getId());

        log.info("[Payment][{}] Completed refund for orderId={}", EVENT, event.getOrderId());
    }

    @Transactional
    public void handleRetry(OrderRefundRequestedEvent event, UUID failedEventId) {
        log.info("[Retry][Payment][{}] Processing orderId={}", EVENT, event.getOrderId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info("[Retry][Payment][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }
}
