package com.teamsolution.order.repository;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.jpa.repository.BaseSoftDeleteRepository;
import com.teamsolution.order.entity.Order;
import com.teamsolution.order.enums.OrderCancelReason;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends BaseSoftDeleteRepository<Order, UUID> {

  Optional<Order> findByIdAndCustomerIdAndIsDeletedFalse(UUID id, UUID customerId);

  Optional<Order> findByIdAndIsDeletedFalse(UUID id);

  List<Order> findByStatusAndPaymentMethodNotAndCreatedAtBeforeAndIsDeletedFalse(
      OrderStatus status, OrderPaymentMethod method, LocalDateTime createdBefore);

  @Modifying
  @Query(
      "UPDATE Order o "
          + "SET o.status = :status, "
          + "o.cancelReason = :cancelReason, "
          + "o.cancelNote = :cancelNote "
          + "WHERE o.id IN :ids")
  void bulkUpdateStatus(
      @Param("ids") List<UUID> ids,
      @Param("status") OrderStatus status,
      @Param("cancelReason") OrderCancelReason cancelReason,
      @Param("cancelNote") String cancelNote);

  List<Order> findByStatusAndDeliveredAtBeforeAndIsDeletedFalse(
      OrderStatus status, LocalDateTime deliveredBefore);
}
