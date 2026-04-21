package com.teamsolution.common.core.enums.order;

import java.util.Set;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,

    CANCEL_REQUESTED,
    CANCELLING,
    CANCELLING_INVENTORY,
    CANCELLING_REFUND,
    CANCELLED,

    RETURN_REQUESTED,
    RETURNING,
    RETURNED,
    RETURN_REJECTED,

    REFUNDING,
    REFUNDED,
    REFUND_FAILED;

    public static final Set<OrderStatus> CANCELLABLE = Set.of(PENDING, CONFIRMED);
    public static final Set<OrderStatus> CANCEL_REQUESTABLE = Set.of(PROCESSING);
    public static final Set<OrderStatus> UPDATABLE = Set.of(PENDING, CONFIRMED);
    public static final Set<OrderStatus> RETURNABLE = Set.of(DELIVERED, COMPLETED);

    private static final Set<OrderStatus> NON_HANDLE_INVENTORY_FAILURE_STATES = Set.of(CANCELLING);
    public boolean shouldSkipInventoryFailureHandling() {
        return NON_HANDLE_INVENTORY_FAILURE_STATES.contains(this);
    }

    public static final Set<OrderStatus> REFUND_PROCESSABLE = Set.of(
            CANCELLING,
            REFUNDING,
            RETURNING,
            RETURNED);
    public boolean canProcessRefund() {
        return REFUND_PROCESSABLE.contains(this);
    }

    public static final Set<OrderStatus> PAYMENT_PROCESSABLE = Set.of(
            PENDING,
            DELIVERED
    );
    public boolean canProcessPay() {
        return PAYMENT_PROCESSABLE.contains(this);
    }

}
