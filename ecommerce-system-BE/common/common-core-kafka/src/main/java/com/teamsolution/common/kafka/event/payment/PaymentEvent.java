package com.teamsolution.common.kafka.event.payment;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.kafka.enums.PaymentEventStatus;
import com.teamsolution.common.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentEvent
        extends BaseEvent {

    private UUID accountRoleId;
    private UUID accountId;
    private UUID customerId;
    private String email;
    private UUID orderId;
    private String orderNumber;
    private PaymentEventStatus status;
    private List<NotificationChannel> channels;
}