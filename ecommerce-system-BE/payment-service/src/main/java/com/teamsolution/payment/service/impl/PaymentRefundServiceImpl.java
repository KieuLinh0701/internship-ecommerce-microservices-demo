package com.teamsolution.payment.service.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.kafka.enums.PaymentEventStatus;
import com.teamsolution.payment.entity.Payment;
import com.teamsolution.payment.entity.PaymentRefund;
import com.teamsolution.payment.enums.PaymentRefundStatus;
import com.teamsolution.payment.kafka.producer.PaymentProducer;
import com.teamsolution.payment.repository.PaymentRefundRepository;
import com.teamsolution.payment.service.PaymentGatewayService;
import com.teamsolution.payment.service.PaymentRefundService;
import com.teamsolution.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRefundServiceImpl
        implements PaymentRefundService {

    private final PaymentService paymentService;
    private final PaymentProducer paymentProducer;
    private final PaymentRefundRepository paymentRefundRepository;
    private final Map<String, PaymentGatewayService> paymentGatewayServiceMap;

    @Override
    @Transactional
    public void refund(
            UUID orderId,
            String orderNumber,
            UUID customerId,
            String email,
            UUID accountId,
            UUID accountRoleId){
        Payment payment = paymentService.findSuccessfulPaymentByOrderId(orderId, customerId);

        PaymentGatewayService service = paymentGatewayServiceMap.get(payment.getMethod().name());

        if (service == null) {
            log.error("[Payment] Unsupported payment method for orderId={}, method={}",
                    orderId, payment.getMethod());

            paymentRefundRepository.save(buildRefund(payment, PaymentRefundStatus.FAILED));

            paymentProducer.publishPaymentEvent(
                    accountId,
                    accountRoleId,
                    customerId,
                    email,
                    orderId,
                    orderNumber,
                    PaymentEventStatus.REFUND_FAILED,
                    List.of(NotificationChannel.WEB)
            );
            return;
        }

        try {
            service.refund(payment.getTransactionId(), payment.getAmount());

            paymentRefundRepository.save(buildRefund(payment, PaymentRefundStatus.PROCESSED));
            paymentProducer.publishPaymentEvent(
                    accountId,
                    accountRoleId,
                    customerId,
                    email,
                    orderId,
                    orderNumber,
                    PaymentEventStatus.REFUND_COMPLETED,
                    List.of(NotificationChannel.WEB)
            );

        } catch (Exception e) {
            log.error("[Payment] Refund failed for orderId={}, reason={}", orderId, e.getMessage());

            paymentRefundRepository.save(buildRefund(payment, PaymentRefundStatus.FAILED));
            paymentProducer.publishPaymentEvent(
                    accountId,
                    accountRoleId,
                    customerId,
                    email,
                    orderId,
                    orderNumber,
                    PaymentEventStatus.REFUND_FAILED,
                    List.of(NotificationChannel.WEB)
            );
        }
    }

    private PaymentRefund buildRefund(Payment payment, PaymentRefundStatus status) {
        return PaymentRefund.builder()
                .payment(payment)
                .amount(payment.getAmount())
                .status(status)
                .processedAt(LocalDateTime.now())
                .build();
    }
}