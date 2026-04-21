package com.teamsolution.cart.controller;

import com.teamsolution.cart.dto.response.CartResponse;
import com.teamsolution.cart.service.customer.CartService;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {

        UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

        CartResponse cartDto = cartService.getCartByAccountId(currentCustomerId);
        return ResponseEntity.ok(ApiResponse.success(cartDto));
    }

    // Delete all cart's items
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteCart() {
        UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

        cartService.removeCart(currentCustomerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}