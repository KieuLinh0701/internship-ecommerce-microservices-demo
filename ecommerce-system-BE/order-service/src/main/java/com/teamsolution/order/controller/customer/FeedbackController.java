package com.teamsolution.order.controller.customer;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.order.dto.response.OrderItemFeedbackResponse;
import com.teamsolution.order.service.customer.OrderItemFeedbackService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class FeedbackController {
  private final OrderItemFeedbackService feedbackService;

  @GetMapping("/{orderId}/feedbacks")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponse<List<OrderItemFeedbackResponse>>> getFeedbacksByOrder(
      @PathVariable UUID orderId) {
    UUID customerId = SecurityUtils.getCurrentCustomerId();

    List<OrderItemFeedbackResponse> feedbacks =
        feedbackService.getFeedbacksByOrderNumber(customerId, orderId);

    return ResponseEntity.ok(ApiResponse.success(feedbacks));
  }

  //    @PostMapping("/{orderId}/feedbacks")
  //    @PreAuthorize("hasRole('CUSTOMER')")
  //    public ResponseEntity<ApiResponse<List<OrderItemFeedbackResponse>>> createFeedbacks(
  //            @PathVariable UUID orderId,
  //            @Valid @RequestBody CreateOrderFeedbackRequest request
  //    ) {
  //        UUID accountId = SecurityUtils.getCurrentAccountId();
  //
  //        List<OrderItemFeedbackResponse> feedbacks =
  //                feedbackService.createOrderFeedback(accountId, orderId, request);
  //
  //        return ResponseEntity.ok(ApiResponse.success(feedbacks));
  //    }

  //    @PutMapping("/feedbacks/{id}")
  //    @PreAuthorize("hasRole('CUSTOMER')")
  //    public ResponseEntity<ApiResponse<OrderItemFeedbackDto>> updateFeedback(
  //            @PathVariable UUID id,
  //            @Valid @RequestBody UpdateFeedbackRequest request
  //    ) {
  //        UUID accountId = SecurityUtils.getCurrentAccountId();
  //
  //        OrderItemFeedbackDto feedback = feedbackService.updateFeedback(accountId, id, request);
  //
  //        return ResponseEntity.ok(ApiResponse.success(feedback));
  //    }
  //
  //    @DeleteMapping("/feedbacks/{id}")
  //    @PreAuthorize("hasRole('CUSTOMER')")
  //    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
  //            @PathVariable UUID id
  //    ) {
  //        UUID accountId = SecurityUtils.getCurrentAccountId();
  //
  //        feedbackService.deleteFeedback(accountId, id);
  //        return ResponseEntity.noContent().build();
  //    }
}
