package com.teamsolution.cart.dto.response;

import com.teamsolution.cart.dto.response.cartitem.CartItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
  private UUID id;
  private List<CartItemResponse> cartItems;
}
