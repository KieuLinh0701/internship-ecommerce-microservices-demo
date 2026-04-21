package com.teamsolution.payment.enums;

public enum EntityName {
    PAYMENT,
    PAYMENT_REFUND;

    public String getValue() {
        return name();
    }
}
