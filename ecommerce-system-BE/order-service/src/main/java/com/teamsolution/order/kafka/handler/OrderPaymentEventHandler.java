package com.teamsolution.order.kafka.handler;

import com.teamsolution.common.kafka.enums.PaymentEventStatus;
import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.order.service.internal.FailedEventSaverService;
import com.teamsolution.order.service.internal.OrderInternalService;
import com.teamsolution.order.service.internal.ProcessedEventService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentEventHandler {

  private static final String EVENT = "PaymentEvent";

  private final OrderInternalService orderInternalService;
  private final ProcessedEventService processedEventService;
  private final FailedEventSaverService failedEventSaverService;

  @Transactional
  public void handle(PaymentEvent event) {
    log.info(
        "[Order][{}] Processing orderId={}, status={}",
        EVENT,
        event.getOrderId(),
        event.getStatus());

    switch (event.getStatus()) {
      case PaymentEventStatus.PAYMENT_COMPLETED -> handlePaymentCompleted(event);
      case PaymentEventStatus.REFUND_COMPLETED -> handleRefundCompleted(event);
      case PaymentEventStatus.PAYMENT_FAILED -> handlePaymentFailed(event);
      case PaymentEventStatus.REFUND_FAILED -> handleRefundFailed(event);
      default ->
          log.warn(
              "[Order][{}] Unknown payment status, skipping orderId={}, status={}",
              EVENT,
              event.getOrderId(),
              event.getStatus());
    }

    processedEventService.markProcessed(event.getId());

    log.info(
        "[Order][{}] Completed orderId={}, status={}",
        EVENT,
        event.getOrderId(),
        event.getStatus());
  }

  @Transactional
  public void handleRetry(PaymentEvent event, UUID failedEventId) {
    log.info(
        "[Retry][Order][{}] Processing orderId={}, status={}",
        EVENT,
        event.getOrderId(),
        event.getStatus());

    handle(event);

    failedEventSaverService.markSuccess(failedEventId);

    log.info("[Retry][Order][{}] Completed orderId={}", EVENT, event.getOrderId());
  }

  private void handleRefundCompleted(PaymentEvent event) {
    orderInternalService.handleRefundCompleted(event.getOrderId(), event.getCustomerId());

    log.info("[Order] Refund completed handled for orderId={}", event.getOrderId());
  }

  private void handleRefundFailed(PaymentEvent event) {
    orderInternalService.handleRefundFailed(event.getOrderId(), event.getCustomerId());

    log.info("[Order] Refund failed handled for orderId={}", event.getOrderId());
  }

  private void handlePaymentCompleted(PaymentEvent event) {
    orderInternalService.handlePaymentCompleted(event.getOrderId(), event.getCustomerId());

    log.info("[Order] Payment completed handled for orderId={}", event.getOrderId());
  }

  private void handlePaymentFailed(PaymentEvent event) {
    orderInternalService.handlePaymentFailed(event.getOrderId(), event.getCustomerId());

    log.info("[Order] Payment failed handled for orderId={}", event.getOrderId());
  }
}
