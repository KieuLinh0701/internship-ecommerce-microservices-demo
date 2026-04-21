package com.teamsolution.notification.kafka.handler;

import com.teamsolution.common.kafka.constant.NotificationEventType;
import com.teamsolution.common.kafka.constant.NotificationVariables;
import com.teamsolution.common.kafka.event.order.OrderCancelRequestedEvent;
import com.teamsolution.notification.grpc.client.AuthGrpcClient;
import com.teamsolution.notification.service.FailedEventSaverService;
import com.teamsolution.notification.service.NotificationDispatcher;
import com.teamsolution.notification.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelRequestedEventHandler {

    private final NotificationDispatcher notificationDispatcher;
    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;
    private final AuthGrpcClient authGrpcClient;

    @Transactional
    public void handle(OrderCancelRequestedEvent event) {
        log.info("[Notification] Fetching admin accounts for notification, orderId={}", event.getOrderId());

        Map<UUID, UUID> adminAccountIds = authGrpcClient.getAllAdminAccountIdAndAccountRoleId();

        if (adminAccountIds.isEmpty()) {
            log.warn("[Notification] No admin accounts found, skip sending notification, orderId={}",
                    event.getOrderId());
            return;
        }

        log.info("[Notification] Found {} admin accounts, orderId={}", adminAccountIds.size(), event.getOrderId());

        Map<String, String> variables = Map.of(NotificationVariables.ORDER_NUMBER, event.getOrderNumber());
        String eventType = NotificationEventType.ORDER_CANCEL_REQUESTED;

        adminAccountIds.forEach((accountId, accountRoleId) -> {
            log.debug(
                    "[Notification] Sending cancel request notification to accountId={}, roleId={}, orderId={}",
                    accountId, accountRoleId, event.getOrderId());
            notificationDispatcher.sendWeb(accountId, accountRoleId, eventType, variables);
        });

        processedEventService.markProcessed(event.getId());

        log.info("[Notification] Completed sending OrderCancelRequestedEvent notifications, orderId={}",
                event.getOrderId());
    }

    @Transactional
    public void handleRetry(OrderCancelRequestedEvent event, UUID failedEventId) {
        log.info(
                "[Notification][Retry][OrderCancelRequestedEvent] Received event id={}, orderId={}",
                event.getId(),
                event.getOrderId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info(
                "[Notification][Retry][OrderCancelRequestedEvent] Completed notification for orderId={}",
                event.getOrderId());
    }
}
