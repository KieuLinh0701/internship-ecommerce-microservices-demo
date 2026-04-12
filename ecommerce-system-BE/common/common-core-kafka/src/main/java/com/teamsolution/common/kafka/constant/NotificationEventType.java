package com.teamsolution.common.kafka.constant;

public class NotificationEventType {

  public static final String FORGOT_PASSWORD = "FORGOT_PASSWORD";
  public static final String EMAIL_VERIFICATION = "VERIFICATION_EMAIL";
  public static final String ORDER_FAILED_INVENTORY = "ORDER_FAILED_INVENTORY";
  public static final String ORDER_FAILED_REFUND_FAILED = "ORDER_FAILED_REFUND_FAILED";
  public static final String ORDER_FAILED_REFUND_COMPLETED = "ORDER_FAILED_REFUND_COMPLETED";
  public static final String ORDER_CANCELLED_REFUND_COMPLETED = "ORDER_CANCELLED_REFUND_COMPLETED";
  public static final String ORDER_CANCELLED_REFUND_FAILED = "ORDER_CANCELLED_REFUND_FAILED";
  public static final String ORDER_PAYMENT_COMPLETED = "ORDER_PAYMENT_COMPLETED";
  public static final String ORDER_PAYMENT_FAILED = "ORDER_PAYMENT_FAILED";
  public static final String ORDER_RETURN_REQUESTED = "ORDER_RETURN_REQUESTED";
  public static final String ORDER_CANCEL_REQUESTED = "ORDER_CANCEL_REQUESTED";
  public static final String CHANGE_EMAIL = "CHANGE_EMAIL";
}
