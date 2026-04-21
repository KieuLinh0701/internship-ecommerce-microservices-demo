package com.teamsolution.order.service.customer;

import com.teamsolution.order.dto.request.CancelOrderRequest;
import com.teamsolution.order.dto.request.CreateOrderRequest;
import com.teamsolution.order.dto.request.UpdateOrderRequest;
import com.teamsolution.order.dto.response.CreateOrderResponse;
import com.teamsolution.order.dto.response.OrderDetailResponse;
import com.teamsolution.order.dto.response.OrderStatusHistoryResponse;
import java.util.List;
import java.util.UUID;

public interface OrderService {

  //    Page<OrderListDto> getOrders(UUID customerId, Pageable pageable, OrderFilterRequest
  // request);

  OrderDetailResponse getOrderById(UUID customerId, UUID orderId);

  CreateOrderResponse createOrder(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      CreateOrderRequest request,
      String clientIp);

  OrderDetailResponse updateOrder(
      UUID accountId, UUID customerId, UUID orderId, UpdateOrderRequest request);

  List<OrderStatusHistoryResponse> getOrderTracking(UUID customerId, UUID orderId);

  OrderDetailResponse cancelOrder(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      UUID orderId,
      CancelOrderRequest request);

  //    void handleRefundCompleted(UUID orderId);
  //    void handleRefundFailed(UUID orderId);

  //    List<Order> bulkCancelExpiredUnpaidOrders();
  //
  //    void handlePaymentCompleted(
  //            UUID orderId,
  //            String orderNumber,
  //            UUID accountId
  //    );
  //
  //    void handlePaymentFailed(
  //            UUID orderId,
  //            String orderNumber,
  //            UUID accountId
  //    );
  //
  //    void handleInventoryFailed(UUID orderId, UUID accountId, UUID accountRoleId, UUID
  // customerId);
  //
  //    Order findByIdAndCustomerId(UUID orderId, UUID customerId);
}
