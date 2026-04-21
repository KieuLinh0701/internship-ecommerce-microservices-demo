package com.teamsolution.payment.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // payment
    UNSUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "Unsupported payment method"),
    PAYMENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create payment"),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "Invalid signature"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment failed"),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "Invalid amount"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "Payment already processed"),

    // refund
    REFUND_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "Refund amount exceeded payment amount"),
    REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process refund"),

    // Customer service - gRPC
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
    CUSTOMER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Customer service unavailable"),

    // ErrorCode.java - thêm vào
    HMAC_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate HMAC signature"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}