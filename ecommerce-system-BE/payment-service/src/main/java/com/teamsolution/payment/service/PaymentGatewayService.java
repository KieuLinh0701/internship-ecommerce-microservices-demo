package com.teamsolution.payment.service;

import com.teamsolution.payment.dto.request.CreatePaymentRequest;

import java.util.UUID;

public interface PaymentGatewayService {
    String createPayment(CreatePaymentRequest request, String clientIp, UUID customerId);
    void refund(String transactionId, Long amount);
}
