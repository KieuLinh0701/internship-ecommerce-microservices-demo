package com.teamsolution.order.service.customer;

import com.teamsolution.order.dto.response.OrderItemFeedbackResponse;
import java.util.List;
import java.util.UUID;

public interface OrderItemFeedbackService {

  List<OrderItemFeedbackResponse> getFeedbacksByOrderNumber(UUID customerId, UUID orderId);

  //    List<OrderItemFeedbackResponse> createOrderFeedback(
  //            UUID accountId,
  //            UUID orderId,
  //            CreateOrderFeedbackRequest request);
  //
  //    OrderItemFeedbackDto updateFeedback(
  //            UUID accountId,
  //            UUID feedbackId,
  //            UpdateFeedbackRequest request);
  //
  //    void deleteFeedback(UUID accountId, UUID feedbackId);
}
