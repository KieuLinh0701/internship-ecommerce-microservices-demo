package com.teamsolution.notification.template;

import com.teamsolution.common.kafka.constant.NotificationEventType;
import com.teamsolution.common.kafka.constant.NotificationVariables;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationTemplateResolver {

  // EMAIL
  public String resolveEmailSubject(String type) {
    return switch (type) {
      case NotificationEventType.EMAIL_VERIFICATION -> "Email Verification";
      case NotificationEventType.FORGOT_PASSWORD -> "Password Reset";
      case NotificationEventType.CHANGE_EMAIL -> "Change Email";
      default -> "Notification";
    };
  }

  public String resolveEmailBody(String type, Map<String, String> variables) {
    return switch (type) {
      case NotificationEventType.EMAIL_VERIFICATION ->
          "Your verification OTP: "
              + variables.get(NotificationVariables.OTP)
              + " (expires in "
              + variables.get(NotificationVariables.EXPIRED_IN)
              + " minutes)";
      case NotificationEventType.FORGOT_PASSWORD ->
          "Your password reset OTP: "
              + variables.get(NotificationVariables.OTP)
              + " (expires in "
              + variables.get(NotificationVariables.EXPIRED_IN)
              + " minutes)";
      case NotificationEventType.CHANGE_EMAIL ->
          "Your change email OTP: "
              + variables.get(NotificationVariables.OTP)
              + " (expires in "
              + variables.get(NotificationVariables.EXPIRED_IN)
              + " minutes)";
      default -> "";
    };
  }

  // WEB
  public String resolveWebTitle(String type) {
    return switch (type) {
      case NotificationEventType.FORGOT_PASSWORD -> "Password Reset Request";
      case NotificationEventType.ORDER_FAILED_INVENTORY -> "Order Failed";
      case NotificationEventType.ORDER_FAILED_REFUND_FAILED -> "Refund Issue";
      case NotificationEventType.ORDER_FAILED_REFUND_COMPLETED -> "Refund Processed";
      case NotificationEventType.ORDER_RETURN_REQUESTED -> "Return Requested";
      case NotificationEventType.ORDER_CANCEL_REQUESTED -> "Cancel Requested";
      case NotificationEventType.ORDER_CANCELLED_REFUND_COMPLETED -> "Cancelled Refund Processed";
      case NotificationEventType.ORDER_CANCELLED_REFUND_FAILED -> "System Alert";
      default -> "Notification";
    };
  }

  public String resolveWebMessage(String type, Map<String, String> variables) {
    return switch (type) {
      case NotificationEventType.FORGOT_PASSWORD ->
          "Someone requested a password reset for your account. If this wasn't you, please ignore.";
      case NotificationEventType.ORDER_FAILED_INVENTORY ->
          "We're sorry, your order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + " could not be completed as the item is no longer available. If you've made a payment, a full refund will be processed shortly.";
      case NotificationEventType.ORDER_FAILED_REFUND_FAILED ->
          "We're sorry, we were unable to process the refund for your order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + ". Please contact our support team for assistance.";
      case NotificationEventType.ORDER_FAILED_REFUND_COMPLETED ->
          "Your refund for order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + " has been successfully returned to your account.";
      case NotificationEventType.ORDER_RETURN_REQUESTED ->
          "A return request has been made for order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + ". Please review and process it promptly.";
      case NotificationEventType.ORDER_CANCEL_REQUESTED ->
          "A cancel request has been made for order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + ". Please review and process it promptly.";
      case NotificationEventType.ORDER_CANCELLED_REFUND_COMPLETED ->
          "Your cancelled order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + " has been successfully refunded to your account.";
      case NotificationEventType.ORDER_CANCELLED_REFUND_FAILED ->
          "System alert: Refund for cancelled order #"
              + variables.get(NotificationVariables.ORDER_NUMBER)
              + " failed. Please check the system and process manually.";
      default -> "You have a new notification.";
    };
  }
}
