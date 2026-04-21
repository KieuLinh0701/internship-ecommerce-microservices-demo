package com.teamsolution.payment.service;

import com.teamsolution.payment.entity.Payment;
import com.teamsolution.payment.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public interface PaymentService {

    void createPendingPayment(UUID customerId, String orderId, Long amount, PaymentMethod method, String txnRef);

    void markPaymentPaid(String txnRef, Map<String, String> gatewayResponse);

    void markPaymentFailed(String txnRef, Map<String, String> gatewayResponse);

    Payment findSuccessfulPaymentByOrderId(UUID orderId, UUID customerId);

    boolean isProcessed(String txnRef);

    boolean isValidAmount(String txnRef, String vnpAmount);

    boolean exists(String txnRef);
}