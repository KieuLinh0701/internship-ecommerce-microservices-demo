package com.teamsolution.order.utils;

import com.teamsolution.order.enums.OrderItemFeedbackStatus;

public class FeedbackPolicyUtils {
  public static final long CREATE_DAYS = 7;
  public static final long UPDATED_DAYS = 3;
  public static final long DELETE_DAYS = 1;

  public static boolean canUpdate(OrderItemFeedbackStatus status) {
    return status == OrderItemFeedbackStatus.PENDING;
  }

  public static boolean canDelete(OrderItemFeedbackStatus status) {
    return status == OrderItemFeedbackStatus.PENDING;
  }
}
