package com.teamsolution.common.core.enums.order;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;

import java.util.EnumSet;

public enum OrderPaymentStatus {
    PAID,
    UNPAID,
    PAYMENT_FAILED,
    REFUND,
    REFUNDING,
    REFUND_FAILED;

    private static final EnumSet<OrderPaymentStatus> VALID_REFUND_COMPLETED_STATES =
            EnumSet.of(REFUNDING, REFUND_FAILED);

    public boolean canTransitionToRefundCompleted() {
        return VALID_REFUND_COMPLETED_STATES.contains(this);
    }

    private static final EnumSet<OrderPaymentStatus> VALID_REFUND_FAILED_STATES =
            EnumSet.of(REFUNDING);

    public boolean canTransitionToRefundFailed() {
        return VALID_REFUND_FAILED_STATES.contains(this);
    }

    private static final EnumSet<OrderPaymentStatus> VALID_PAID_STATES =
            EnumSet.of(UNPAID, PAYMENT_FAILED);

    public boolean canTransitionToPaid() {
        return VALID_PAID_STATES.contains(this);
    }

    private static final EnumSet<OrderPaymentStatus> VALID_PAYMENT_FAILED_STATES =
            EnumSet.of(UNPAID);

    public boolean canTransitionToPaymentFailed() {
        return VALID_PAYMENT_FAILED_STATES.contains(this);
    }

    public static OrderPaymentStatus from(String value) {
        if (value == null) {
            return UNPAID;
        }

        try {
            return OrderPaymentStatus.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new AppException(CommonErrorCode.UNEXPECTED_ENUM_VALUE);
        }
    }
}
