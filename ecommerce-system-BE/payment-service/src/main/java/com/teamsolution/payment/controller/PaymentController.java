package com.teamsolution.payment.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.payment.dto.request.CreatePaymentRequest;
import com.teamsolution.payment.exception.ErrorCode;
import com.teamsolution.payment.service.PaymentGatewayService;
import com.teamsolution.payment.service.PaymentRefundService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final Map<String, PaymentGatewayService> paymentGatewayServiceMap;
    private final PaymentRefundService paymentRefundService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> create(
            @Valid @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest) {

        String ipAddr = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddr == null) {
            ipAddr = httpRequest.getRemoteAddr();
        }

        UUID customerId = SecurityUtils.getCurrentCustomerId();

        PaymentGatewayService service = paymentGatewayServiceMap.get(request.method());
        if (service == null) {
            throw new AppException(ErrorCode.UNSUPPORTED_PAYMENT_METHOD);
        }

        return ResponseEntity.ok(ApiResponse.success(service.createPayment(request, ipAddr, customerId)));
    }
}