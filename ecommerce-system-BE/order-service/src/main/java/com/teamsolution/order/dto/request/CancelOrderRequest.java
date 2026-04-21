package com.teamsolution.order.dto.request;

import com.teamsolution.order.enums.OrderCancelReason;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record CancelOrderRequest(
    @NotNull(message = "Cancel reason is required") OrderCancelReason reason, String cancelNote) {

  @AssertTrue(message = "Cancel note is required when reason is OTHER")
  public boolean isCancelNoteValid() {
    if (OrderCancelReason.OTHER == reason) {
      return cancelNote != null && !cancelNote.isBlank();
    }
    return true;
  }
}
