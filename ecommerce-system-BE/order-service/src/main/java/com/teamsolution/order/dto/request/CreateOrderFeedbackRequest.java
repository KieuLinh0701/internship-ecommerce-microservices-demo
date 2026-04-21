package com.teamsolution.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateOrderFeedbackRequest(
    @NotEmpty(message = "Feedback items must not be empty") @Valid List<ItemFeedback> items) {
  public record ItemFeedback(
      @NotNull(message = "Order item id is required") UUID orderItemId,
      @NotNull(message = "Rating is required")
          @Min(value = 1, message = "Rating must be at least 1")
          @Max(value = 5, message = "Rating must not exceed 5")
          Short rating,
      @Size(max = 1000, message = "Comment must not exceed 1000 characters") String comment,
      @Size(max = 5, message = "Maximum 5 images allowed")
          List<@NotNull(message = "Image Id must not be blank") UUID> images,
      Boolean isAnonymous) {}
}
