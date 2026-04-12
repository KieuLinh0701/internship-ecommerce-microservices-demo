package com.teamsolution.common.kafka.topics;

public class KafkaTopics {

  // order
  public static final String ORDER_CANCELLED = "order.cancelled";
  public static final String ORDER_CANCEL_REQUESTED = "order.cancelled-requested";
  public static final String ORDER_CREATED = "order.created";
  public static final String ORDER_FAILED = "order.failed";
  public static final String ORDER_CONFIRMED = "order.confirmed";
  public static final String ORDER_REFUND_REQUESTED = "order.refund.requested";
  public static final String ORDER_PAYMENT_TIMEOUT = "order.payment.timeout";
  public static final String ORDER_RETURN_REQUESTED = "order.return-requested";
  public static final String ORDER_PAYMENT = "order.payment";

  // inventory
  public static final String INVENTORY_RESERVATION_FAILED = "inventory.reservation.failed";

  // payment
  public static final String PAYMENT_SUCCESS = "payment.success";

  // Auth
  public static final String AUTH_NOTIFICATION = "auth.notification";

  // Inventory
  public static final String PRODUCT_CHANGED = "product.changed";
  public static final String PRODUCT_STATUS_CHANGED = "product.status.changed";
}
