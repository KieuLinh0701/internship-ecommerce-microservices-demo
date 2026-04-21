package com.teamsolution.payment.service;

import java.util.Map;

public interface VNPayService extends PaymentGatewayService {
    boolean verifyHash(Map<String, String> params, String vnpSecureHash);
    String handleIpn(Map<String, String> allParams);
    void handleResult(Map<String, String> allParams);
}