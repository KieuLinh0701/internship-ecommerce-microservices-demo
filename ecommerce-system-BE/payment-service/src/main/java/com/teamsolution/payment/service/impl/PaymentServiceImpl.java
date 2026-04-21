package com.teamsolution.payment.service.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.kafka.enums.PaymentEventStatus;
import com.teamsolution.payment.entity.Payment;
import com.teamsolution.payment.enums.PaymentMethod;
import com.teamsolution.payment.enums.PaymentStatus;
import com.teamsolution.payment.exception.ErrorCode;
import com.teamsolution.payment.kafka.producer.PaymentProducer;
import com.teamsolution.payment.repository.PaymentRepository;
import com.teamsolution.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Override
    public void createPendingPayment(
            UUID customerId,
            String orderId,
            Long amount,
            PaymentMethod method,
            String txnRef) {
        Payment payment = Payment.builder()
                .orderId(UUID.fromString(orderId))
                .customerId(customerId)
                .method(method)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .idempotencyKey(txnRef)
                .build();
        paymentRepository.save(payment);
    }

    @Override
    public void markPaymentPaid(String txnRef, Map<String, String> gatewayResponse) {
        Payment payment = findByIdempotencyKey(txnRef);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCompletedAt(LocalDateTime.now());
        payment.setGatewayResponse(gatewayResponse.toString());
        payment.setTransactionId(gatewayResponse.get("vnp_TransactionNo"));
        paymentRepository.save(payment);

        paymentProducer.publishPaymentEvent(
                null,
                null,
                payment.getCustomerId(),
                null,
                payment.getOrderId(),
                null,
                PaymentEventStatus.PAYMENT_COMPLETED,
                List.of(NotificationChannel.WEB)
        );
    }

    @Override
    public void markPaymentFailed(String txnRef, Map<String, String> gatewayResponse) {
        Payment payment = findByIdempotencyKey(txnRef);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setGatewayResponse(gatewayResponse.toString());
        paymentRepository.save(payment);

        paymentProducer.publishPaymentEvent(
                null,
                null,
                payment.getCustomerId(),
                null,
                payment.getOrderId(),
                null,
                PaymentEventStatus.PAYMENT_FAILED,
                List.of(NotificationChannel.WEB)
        );
    }

    @Override
    public Payment findSuccessfulPaymentByOrderId(UUID orderId, UUID customerId) {
        return paymentRepository
                .findByOrderIdAndCustomerIdAndStatus(orderId, customerId, PaymentStatus.SUCCESS)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Override
    public boolean isProcessed(String txnRef) {
        return paymentRepository
                .findByIdempotencyKey(txnRef)
                .map(payment -> payment.getStatus() == PaymentStatus.SUCCESS
                        || payment.getStatus() == PaymentStatus.FAILED)
                .orElse(false);
    }

    @Override
    public boolean isValidAmount(String txnRef, String vnpAmount) {

        Payment payment = findByIdempotencyKey(txnRef);

        long requestAmount = Long.parseLong(vnpAmount) / 100;
        return payment.getAmount().equals(requestAmount);
    }

    @Override
    public boolean exists(String txnRef) {
        return paymentRepository.existsByIdempotencyKey(txnRef);
    }

    private Payment findByIdempotencyKey(String idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}