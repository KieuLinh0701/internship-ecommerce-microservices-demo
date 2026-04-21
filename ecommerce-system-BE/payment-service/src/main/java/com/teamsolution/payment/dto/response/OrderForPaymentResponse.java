package com.teamsolution.payment.dto.response;

import com.teamsolution.common.core.enums.order.OrderPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderForPaymentResponse {
    private long total;
    private OrderPaymentStatus paymentStatus;
}
