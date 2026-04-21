package com.teamsolution.order.controller.customer;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.order.dto.request.CancelOrderRequest;
import com.teamsolution.order.dto.request.CreateOrderRequest;
import com.teamsolution.order.dto.request.UpdateOrderRequest;
import com.teamsolution.order.dto.response.CreateOrderResponse;
import com.teamsolution.order.dto.response.OrderDetailResponse;
import com.teamsolution.order.dto.response.OrderStatusHistoryResponse;
import com.teamsolution.order.service.customer.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  //    @GetMapping
  //    @PreAuthorize("hasRole('CUSTOMER')")
  //    public ResponseEntity<ApiResponse<PageResponse<OrderListDto>>> getOrders(
  //            OrderFilterRequest filterRequest) {
  //        Pageable pageable =
  //                PageableUtils.toPageable(
  //                        filterRequest.getPage(),
  //                        filterRequest.getSize(),
  //                        filterRequest.getSortBy(),
  //                        filterRequest.getDirection(),
  //                        false);
  //
  //        UUID customerId = SecurityUtils.getCurrentCustomerId();
  //
  //        Page<OrderListDto> orders = orderService.getOrders(customerId, pageable, filterRequest);
  //        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(orders)));
  //    }

  @GetMapping("/{orderId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderById(@PathVariable UUID orderId) {

    UUID customerId = SecurityUtils.getCurrentCustomerId();

    OrderDetailResponse order = orderService.getOrderById(customerId, orderId);
    return ResponseEntity.ok(ApiResponse.success(order));
  }

  @PostMapping
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
      @Valid @RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {

    String ipAddr = httpRequest.getHeader("X-Forwarded-For");
    if (ipAddr == null) {
      ipAddr = httpRequest.getRemoteAddr();
    }

    log.info("[OrderController] createOrder request received from IP: {}", ipAddr);

    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();
    UUID customerId = SecurityUtils.getCurrentCustomerId();

    CreateOrderResponse response =
        orderService.createOrder(accountId, accountRoleId, customerId, request, ipAddr);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PatchMapping("/{orderId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrder(
      @RequestBody UpdateOrderRequest request, @PathVariable UUID orderId) {
    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID customerId = SecurityUtils.getCurrentCustomerId();

    OrderDetailResponse dto = orderService.updateOrder(accountId, customerId, orderId, request);
    return ResponseEntity.ok(ApiResponse.success(dto));
  }

  @GetMapping("/{orderId}/tracking")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponse<List<OrderStatusHistoryResponse>>> getOrderTracking(
      @PathVariable UUID orderId) {

    UUID customerId = SecurityUtils.getCurrentCustomerId();

    List<OrderStatusHistoryResponse> historyResponses =
        orderService.getOrderTracking(customerId, orderId);
    return ResponseEntity.ok(ApiResponse.success(historyResponses));
  }

  @PatchMapping("/{orderId}/cancel")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<ApiResponse<OrderDetailResponse>> cancelOrder(
      @PathVariable UUID orderId, @Valid @RequestBody CancelOrderRequest request) {
    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();
    UUID customerId = SecurityUtils.getCurrentCustomerId();

    OrderDetailResponse orderDetailResponse =
        orderService.cancelOrder(accountId, accountRoleId, customerId, orderId, request);
    return ResponseEntity.ok(ApiResponse.success(orderDetailResponse));
  }
}
