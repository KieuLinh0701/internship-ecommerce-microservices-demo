package com.teamsolution.cart.service.customer;

import com.teamsolution.cart.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCartByAccountId(UUID customerId);

    void removeCart(UUID customerId);
}
