package com.teamsolution.payment.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.payment.entity.Payment;
import com.teamsolution.payment.enums.PaymentStatus;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository
        extends BaseRepository<Payment, UUID> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByOrderIdAndCustomerIdAndStatus(UUID orderId, UUID customerId, PaymentStatus status);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
