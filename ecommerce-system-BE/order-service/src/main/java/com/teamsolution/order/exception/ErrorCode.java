package com.teamsolution.order.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

  // Order
  ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
  ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "Cannot cancel order at current status"),
  ORDER_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "Cannot update order at current status"),
  INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock for variant"),
  ORDER_PRICE_MISMATCH(HttpStatus.BAD_REQUEST, "Order price mismatch, please review your cart"),
  ORDER_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "Order must be completed to perform this action"),
  ORDER_COMPLETED_AT_NULL(
      HttpStatus.INTERNAL_SERVER_ERROR, "System error: order completion time is null"),
  ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "Order has already been cancelled"),
  ORDER_SKIP_INVENTORY_FAILURE(
      HttpStatus.BAD_REQUEST, "Order %s cannot handle inventory failure in status: %s"),
  ORDER_ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "Order already refunded with id: %s"),
  ORDER_INVALID_STATUS_FOR_REFUND(
      HttpStatus.BAD_REQUEST, "Order with id %s cannot process refund in status: %s"),
  ORDER_INVALID_PAYMENT_STATUS_FOR_REFUND_COMPLETED(
      HttpStatus.BAD_REQUEST,
      "Order with id %s cannot transition to refund completed in payment status: %s"),
  ORDER_INVALID_PAYMENT_STATUS_FOR_REFUND_FAILED(
      HttpStatus.BAD_REQUEST,
      "Order with id %s cannot transition to refund failed in payment status: %s"),
  ORDER_INVALID_STATUS_FOR_PAYMENT(
      HttpStatus.BAD_REQUEST, "Order with id %s cannot process refund in status: %s"),
  ORDER_INVALID_PAYMENT_STATUS_FOR_PAYMENT_COMPLETED(
      HttpStatus.BAD_REQUEST,
      "Order with id %s cannot transition to payment completed in payment status: %s"),
  ORDER_INVALID_PAYMENT_STATUS_FOR_PAYMENT_FAILED(
      HttpStatus.BAD_REQUEST,
      "Order with id %s cannot transition to payment failed in payment status: %s"),

  // Coupon
  COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "Coupon not found"),
  COUPON_INACTIVE(HttpStatus.BAD_REQUEST, "Coupon is inactive"),
  COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "Coupon has expired"),
  COUPON_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "Coupon usage limit reached"),
  COUPON_MIN_ORDER_NOT_MET(HttpStatus.BAD_REQUEST, "Order value does not meet minimum requirement"),
  ORDER_ALREADY_FEEDBACK(HttpStatus.CONFLICT, "Order has already been feedback"),
  DUPLICATE_SHIPPING_COUPON(HttpStatus.BAD_REQUEST, "Only one shipping coupon is allowed"),
  DUPLICATE_PRODUCT_COUPON(HttpStatus.BAD_REQUEST, "Only one product coupon is allowed"),

  // Order Item
  ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Order item not found"),

  // Feedback
  FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "Feedback not found"),
  FEEDBACK_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "Feedback time has expired"),
  FEEDBACK_ALREADY_EXISTS(HttpStatus.CONFLICT, "Feedback already exists for this order item"),
  ORDER_FEEDBACK_NOT_COMPLETE(
      HttpStatus.BAD_REQUEST, "You must provide feedback for all items in the order"),
  FEEDBACK_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "Cannot update feedback at current status"),
  FEEDBACK_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "Cannot delete feedback at current status"),
  FEEDBACK_UPDATE_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "Feedback update time has expired"),
  FEEDBACK_DELETE_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "Feedback delete time has expired"),
  FEEDBACK_ALREADY_UPDATED(HttpStatus.BAD_REQUEST, "Feedback has already been updated"),

  // Feedback Image
  INVALID_FEEDBACK_IMAGE(HttpStatus.BAD_REQUEST, "Invalid feedback image reference"),

  // Customer Service - gRPC
  CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
  CUSTOMER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Customer service unavailable"),
  ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "Address not found"),

  // Inventory Service - gRPC
  PRODUCT_VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product variant not found"),

  // Cart Service - gRPC
  CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart item not found"),

  // Payment Service - grpc
  PAYMENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Customer service unavailable"),
  UNSUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "Unsupported payment method"),
  PAYMENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create payment"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
