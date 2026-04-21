package com.teamsolution.payment.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.payment.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnpayService;

    @GetMapping("/ipn")
    public ResponseEntity<String> ipn(@RequestParam Map<String, String> allParams) {
        return ResponseEntity.ok(vnpayService.handleIpn(allParams));
    }

    @GetMapping("/result")
    public ResponseEntity<ApiResponse<Void>> result(@RequestParam Map<String, String> allParams) {

        vnpayService.handleResult(allParams);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}