package com.teamsolution.payment.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.payment.entity.PaymentRefund;

import java.util.UUID;

public interface PaymentRefundRepository
        extends BaseRepository<PaymentRefund, UUID> {

}
