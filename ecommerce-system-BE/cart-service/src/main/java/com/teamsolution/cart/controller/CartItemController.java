package com.teamsolution.cart.controller;

import com.teamsolution.cart.dto.request.CreateCartItemRequest;
import com.teamsolution.cart.dto.request.UpdateCartItemQuantityRequest;
import com.teamsolution.cart.dto.request.UpdateCartItemVariantRequest;
import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import com.teamsolution.cart.service.customer.CartItemService;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addCartItem(
            @Valid @RequestBody CreateCartItemRequest request
    ) {
        UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

        CartItemResponse cartItemResponse = cartItemService.addCartItem(currentCustomerId, request);
        return ResponseEntity.ok(ApiResponse.success(cartItemResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable UUID id
    ) {
        UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

        cartItemService.deleteCartItem(currentCustomerId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable UUID id,
            @RequestBody UpdateCartItemQuantityRequest request) {
        UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();

        CartItemResponse cartItemResponse = cartItemService.updateCartItemQuantity(currentCustomerId, id, request);

        return ResponseEntity.ok(ApiResponse.success(cartItemResponse));
    }

    @PatchMapping("/{id}/variant")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateVariant(
            @PathVariable UUID id,
            @RequestBody UpdateCartItemVariantRequest request) {
        UUID currentCustomerId = SecurityUtils.getCurrentCustomerId();
        return ResponseEntity.ok(ApiResponse.success(
                cartItemService.updateCartItemVariant(currentCustomerId, id, request)));
    }
}