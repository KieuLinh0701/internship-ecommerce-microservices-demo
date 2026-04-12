package com.teamsolution.cart.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

  // Cart
  CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found"),

  // Cart item
  CART_ITEM_NOT_BELONG_TO_CART(HttpStatus.FORBIDDEN, "Cart item not belong to your cart"),
  CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart Item not found"),

  // Variant
  PRODUCT_VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Variant not found"),

  INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock for the requested quantity"),

  CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
  CUSTOMER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Customer service unavailable"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
