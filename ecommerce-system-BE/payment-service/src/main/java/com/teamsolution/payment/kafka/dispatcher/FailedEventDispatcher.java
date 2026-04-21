package com.teamsolution.payment.kafka.dispatcher;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.common.kafka.event.order.OrderRefundRequestedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.payment.kafka.handler.OrderCancelledEventHandler;
import com.teamsolution.payment.kafka.handler.OrderRefundRequestedEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FailedEventDispatcher {

    private final OrderRefundRequestedEventHandler orderRefundRequestedEventHandler;
    private final OrderCancelledEventHandler orderCancelledEventHandler;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(String topic, Object event, UUID failedEventId) {
        switch (topic) {
            case KafkaTopics.ORDER_CANCELLED ->
                    orderCancelledEventHandler.handleRetry((OrderCancelledEvent) event, failedEventId);
            case KafkaTopics.ORDER_REFUND_REQUESTED ->
                    orderRefundRequestedEventHandler.handleRetry((OrderRefundRequestedEvent) event, failedEventId);
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        }
    }
}
