package com.teamsolution.notification.kafka.handler;

import com.teamsolution.common.kafka.constant.NotificationEventType;
import com.teamsolution.common.kafka.constant.NotificationVariables;
import com.teamsolution.common.kafka.enums.PaymentEventStatus;
import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.notification.grpc.client.AuthGrpcClient;
import com.teamsolution.notification.service.FailedEventSaverService;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentEventHandler {

    private final NotificationDispatcher notificationDispatcher;
    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;
    private final AuthGrpcClient authGrpcClient;

    @Transactional
    public void handle(PaymentEvent event) {
        log.info("[Order] Handling payment/refund event for orderId={}", event.getOrderId());

        if (isRefundFailed(event)) {
            sendAdminNotification(event);
            return;
        }

        sendCustomerNotification(event);
    }

    @Transactional
    public void handleRetry(PaymentEvent event, UUID failedEventId) {
        log.info(
                "[Notification][Retry][PaymentEvent] Received event id={}, orderId={}",
                event.getId(),
                event.getOrderId());

        handle(event); // reuse cùng logic, không duplicate

        failedEventSaverService.markSuccess(failedEventId);
        processedEventService.markProcessed(event.getId());

        log.info(
                "[Notification][Retry][PaymentEvent] Completed notification for orderId={}",
                event.getOrderId());
    }

    private boolean isRefundFailed(PaymentEvent event) {
        return event.getStatus() == PaymentEventStatus.REFUND_FAILED;
    }

    private void sendAdminNotification(PaymentEvent event) {
        Map<UUID, UUID> adminAccountIds = authGrpcClient.getAllAdminAccountIdAndAccountRoleId();
        adminAccountIds.forEach((accountId, accountRoleId) ->
                notificationDispatcher.sendWeb(
                        accountId,
                        accountRoleId,
                        NotificationEventType.ADMIN_ORDER_REFUND_FAILED,
                        Map.of(NotificationVariables.ORDER_NUMBER, event.getOrderNumber()))
        );

        log.info("[Order] Admin notification sent for refund failure, orderId={}", event.getOrderId());
    }

    private void sendCustomerNotification(PaymentEvent event) {
        String type = resolveCustomerNotificationType(event);

        if (type == null) {
            log.warn("[Order] Unknown payment event status for orderId={}, status={}",
                    event.getOrderId(), event.getStatus());
            return;
        }

        notificationDispatcher.send(
                event.getAccountId(),
                event.getAccountRoleId(),
                event.getEmail(),
                type,
                Map.of(NotificationVariables.ORDER_NUMBER, event.getOrderNumber()),
                event.getChannels()
        );

        log.info("[Order] Customer notification sent for orderId={}, type={}", event.getOrderId(), type);
    }

    private static @Nullable String resolveCustomerNotificationType(PaymentEvent event) {
        return switch (event.getStatus()) {
            case REFUND_COMPLETED -> NotificationEventType.ORDER_REFUND_COMPLETED;
            case REFUND_FAILED    -> NotificationEventType.ORDER_REFUND_FAILED;
            default               -> null;
        };
    }
}
