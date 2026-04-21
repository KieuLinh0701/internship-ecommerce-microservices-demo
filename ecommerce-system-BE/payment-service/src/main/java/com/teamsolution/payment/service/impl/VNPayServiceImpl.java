package com.teamsolution.payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.payment.config.properties.VnpayProperties;
import com.teamsolution.payment.constant.VNPayConstants;
import com.teamsolution.payment.dto.request.CreatePaymentRequest;
import com.teamsolution.payment.enums.PaymentMethod;
import com.teamsolution.payment.exception.ErrorCode;
import com.teamsolution.payment.service.PaymentService;
import com.teamsolution.payment.service.VNPayService;
import com.teamsolution.payment.utils.HmacUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service("VNPAY")
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VnpayProperties vnpayProperties;
    private final PaymentService paymentService;

    @Override
    public String createPayment(
            CreatePaymentRequest request,
            String clientIp,
            UUID customerId) throws AppException {
        try {
            log.info("TMN Code: [{}], Hash Secret: [{}]",
                    vnpayProperties.getTmnCode(),
                    vnpayProperties.getHashSecret());

            String tnxRef = UuidUtils.generate().toString();

            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put(VNPayConstants.VNP_VERSION, vnpayProperties.getVersion());
            vnpParams.put(VNPayConstants.VNP_COMMAND, VNPayConstants.COMMAND_PAY);
            vnpParams.put(VNPayConstants.VNP_TMN_CODE, vnpayProperties.getTmnCode());
            vnpParams.put(VNPayConstants.VNP_AMOUNT, String.valueOf(request.amount() * 100));
            vnpParams.put(VNPayConstants.VNP_CURRENCY, VNPayConstants.CURRENCY_VND);
            vnpParams.put(VNPayConstants.VNP_TXN_REF, tnxRef);
            vnpParams.put(VNPayConstants.VNP_ORDER_INFO, request.orderId());
            vnpParams.put(VNPayConstants.VNP_ORDER_TYPE, vnpayProperties.getOrderType());
            vnpParams.put(VNPayConstants.VNP_LOCALE, vnpayProperties.getLocale());
            vnpParams.put(VNPayConstants.VNP_RETURN_URL, vnpayProperties.getReturnUrl());
            vnpParams.put(VNPayConstants.VNP_IPN_URL, vnpayProperties.getIpnUrl());
            vnpParams.put(VNPayConstants.VNP_IP_ADDR, VNPayConstants.LOCAL_IP);
            vnpParams.put(VNPayConstants.VNP_CREATE_DATE,
                    new SimpleDateFormat(VNPayConstants.DATE_PATTERN).format(new Date()));

            paymentService.createPendingPayment(
                    customerId,
                    request.orderId(),
                    request.amount(),
                    PaymentMethod.VNPAY,
                    tnxRef
            );

            return buildPaymentUrl(vnpParams);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PAYMENT_CREATION_FAILED);
        }
    }

    @Override
    public void refund(String transactionId, Long amount) throws AppException {
        try {
            String requestId = UuidUtils.generate().toString();
            String createDate = new SimpleDateFormat(VNPayConstants.DATE_PATTERN).format(new Date());

            Map<String, String> params = new LinkedHashMap<>();
            params.put(VNPayConstants.VNP_REQUEST_ID, requestId);
            params.put(VNPayConstants.VNP_VERSION, vnpayProperties.getVersion());
            params.put(VNPayConstants.VNP_COMMAND, VNPayConstants.COMMAND_REFUND);
            params.put(VNPayConstants.VNP_TMN_CODE, vnpayProperties.getTmnCode());
            params.put(VNPayConstants.VNP_TRANSACTION_TYPE, VNPayConstants.TRANSACTION_TYPE_REFUND);
            params.put(VNPayConstants.VNP_TXN_REF, transactionId);
            params.put(VNPayConstants.VNP_AMOUNT, String.valueOf(amount * 100));
            params.put(
                    VNPayConstants.VNP_ORDER_INFO,
                    String.format(VNPayConstants.REFUND_FORMAT, transactionId, amount)
            );
            params.put(VNPayConstants.VNP_CREATE_BY, VNPayConstants.SYSTEM);
            params.put(VNPayConstants.VNP_CREATE_DATE, createDate);
            params.put(VNPayConstants.VNP_IP_ADDR, VNPayConstants.LOCAL_IP);

            String hash = buildHash(params);
            params.put(VNPayConstants.VNP_SECURE_HASH, hash);

            String response = callVnpayApi(params);

            Map<String, String> result = new ObjectMapper().readValue(response, Map.class);
            if (!VNPayConstants.RESPONSE_SUCCESS.equals(result.get(VNPayConstants.VNP_RESPONSE_CODE))) {
                throw new AppException(ErrorCode.REFUND_FAILED);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.REFUND_FAILED);
        }
    }

    @Override
    public boolean verifyHash(Map<String, String> params, String vnpSecureHash) {
        return buildHash(params).equalsIgnoreCase(vnpSecureHash);
    }

    @Override
    public String handleIpn(Map<String, String> allParams) {
        String vnpSecureHash = allParams.remove(VNPayConstants.VNP_SECURE_HASH);
        allParams.remove(VNPayConstants.VNP_SECURE_HASH_TYPE);

        if (!verifyHash(allParams, vnpSecureHash)) {
            return buildIpnResponse(VNPayConstants.RESPONSE_INVALID_SIGNATURE, VNPayConstants.MSG_INVALID_SIGNATURE);
        }

        String responseCode = allParams.get(VNPayConstants.VNP_RESPONSE_CODE);
        String txnRef = allParams.get(VNPayConstants.VNP_TXN_REF);

        if (paymentService.isProcessed(txnRef)) {
            return buildIpnResponse(VNPayConstants.RESPONSE_ALREADY_PROCESSED, VNPayConstants.MSG_ALREADY_PROCESSED);
        }

        String amount = allParams.get(VNPayConstants.VNP_AMOUNT);

        if (!paymentService.isValidAmount(txnRef, amount)) {
            return buildIpnResponse(VNPayConstants.RESPONSE_INVALID_AMOUNT, VNPayConstants.MSG_INVALID_AMOUNT);
        }

        if (VNPayConstants.RESPONSE_SUCCESS.equals(responseCode)) {
            paymentService.markPaymentPaid(txnRef, allParams);
        } else {
            paymentService.markPaymentFailed(txnRef, allParams);
        }

        return buildIpnResponse(VNPayConstants.RESPONSE_SUCCESS, VNPayConstants.MSG_CONFIRM_SUCCESS);
    }

    @Override
    public void handleResult(Map<String, String> allParams) {
        String vnpSecureHash = allParams.remove(VNPayConstants.VNP_SECURE_HASH);
        allParams.remove(VNPayConstants.VNP_SECURE_HASH_TYPE);

        if (!verifyHash(allParams, vnpSecureHash)) {
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        String responseCode = allParams.get(VNPayConstants.VNP_RESPONSE_CODE);
        String txnRef = allParams.get(VNPayConstants.VNP_TXN_REF);
        String amount = allParams.get(VNPayConstants.VNP_AMOUNT);

        if (!paymentService.isValidAmount(txnRef, amount)) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (!paymentService.exists(txnRef)) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        if (VNPayConstants.RESPONSE_SUCCESS.equals(responseCode)) {
            return;
        }

        throw new AppException(ErrorCode.PAYMENT_FAILED);
    }

    private String buildHash(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (String key : fieldNames) {
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {
                if (!hashData.isEmpty()) hashData.append('&');

                hashData.append(key).append('=').append(value);
            }
        }

        String hash =  HmacUtils.hmacSHA512(
                vnpayProperties.getHashSecret(),
                hashData.toString()
        );
        log.info("Hash data: {}, Hash: {}", hashData, hash);
        return hash;
    }

    private String buildPaymentUrl(Map<String, String> vnpParams) throws Exception {
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String key : fieldNames) {
            String value = vnpParams.get(key);

            if (value != null && !value.isEmpty()) {
                if (!hashData.isEmpty()) {
                    hashData.append('&');
                    query.append('&');
                }

                hashData.append(key).append('=').append(value);

                query.append(URLEncoder.encode(key, UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(value, UTF_8));
            }
        }

        log.info("Hash data: {}", hashData);

        String vnpSecureHash = HmacUtils.hmacSHA512(
                vnpayProperties.getHashSecret(),
                hashData.toString()
        );
        query.append("&vnp_SecureHash=").append(vnpSecureHash);
        return vnpayProperties.getUrl() + "?" + query;
    }

    private String buildIpnResponse(String code, String message) {
        return String.format(VNPayConstants.IPN_RESPONSE_FORMAT, code, message);
    }

    private String callVnpayApi(Map<String, String> params) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String body = new ObjectMapper().writeValueAsString(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(vnpayProperties.getRefundUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
