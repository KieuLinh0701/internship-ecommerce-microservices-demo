package com.teamsolution.order.repository;

import com.teamsolution.common.jpa.repository.BaseSoftDeleteRepository;
import com.teamsolution.order.entity.OrderItemFeedback;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemFeedbackRepository
    extends BaseSoftDeleteRepository<OrderItemFeedback, UUID> {

  @Query(
      """
                SELECT f FROM OrderItemFeedback f
                JOIN f.orderItem oi
                JOIN oi.order o
                WHERE o.id = :orderId
                  AND f.isDeleted = false
            """)
  List<OrderItemFeedback> findByOrderId(UUID orderId);

  Optional<OrderItemFeedback> findByIdAndCustomerIdAndIsDeletedFalse(UUID id, UUID customerId);
}
