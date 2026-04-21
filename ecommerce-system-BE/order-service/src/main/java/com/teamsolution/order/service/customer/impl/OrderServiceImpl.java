package com.teamsolution.order.service.customer.impl;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.order.config.properties.OrderProperties;
import com.teamsolution.order.dto.request.CancelOrderRequest;
import com.teamsolution.order.dto.request.CreateOrderRequest;
import com.teamsolution.order.dto.request.UpdateOrderRequest;
import com.teamsolution.order.dto.response.CreateOrderResponse;
import com.teamsolution.order.dto.response.OrderDetailResponse;
import com.teamsolution.order.dto.response.OrderStatusHistoryResponse;
import com.teamsolution.order.entity.Coupon;
import com.teamsolution.order.entity.Order;
import com.teamsolution.order.entity.OrderCoupon;
import com.teamsolution.order.entity.OrderItem;
import com.teamsolution.order.enums.CouponType;
import com.teamsolution.order.exception.ErrorCode;
import com.teamsolution.order.grpc.client.CartGrpcClient;
import com.teamsolution.order.grpc.client.CustomerGrpcClient;
import com.teamsolution.order.grpc.client.InventoryGrpcClient;
import com.teamsolution.order.grpc.client.PaymentGrpcClient;
import com.teamsolution.order.kafka.producer.OrderProducer;
import com.teamsolution.order.mapper.CreateOrderResponseMapper;
import com.teamsolution.order.mapper.OrderDetailMapper;
import com.teamsolution.order.repository.OrderRepository;
import com.teamsolution.order.service.customer.OrderService;
import com.teamsolution.order.service.internal.CouponInternalService;
import com.teamsolution.order.service.internal.OrderInternalService;
import com.teamsolution.order.service.internal.OrderStatusHistoryInternalService;
import com.teamsolution.proto.grpc.cart.CartItem;
import com.teamsolution.proto.grpc.customer.GetAddressByCustomerIdAndAddressIdResponse;
import com.teamsolution.proto.grpc.inventory.AttributeValue;
import com.teamsolution.proto.grpc.inventory.Variant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private static final long SHIPPING_FEE = 30_000L;

  private final OrderInternalService orderInternalService;
  private final CouponInternalService couponInternalService;
  private final OrderStatusHistoryInternalService orderStatusHistoryInternalService;

  private final OrderRepository orderRepository;

  private final OrderDetailMapper orderDetailMapper;
  private final CreateOrderResponseMapper createOrderResponseMapper;

  private final CustomerGrpcClient customerGrpcClient;
  private final CartGrpcClient cartGrpcClient;
  private final InventoryGrpcClient inventoryGrpcClient;
  private final PaymentGrpcClient paymentGrpcClient;

  private final OrderProperties orderProperties;

  private final TransactionTemplate transactionTemplate;

  private final OrderProducer orderProducer;

  //    @Override
  //    public Page<OrderListDto> getOrders(UUID customerId, Pageable pageable, OrderFilterRequest
  // request) {
  //
  //        Specification<Order> specification = OrderSpecification.build(customerId, request);
  //        Page<Order> orders = orderRepository.findAll(specification, pageable);
  //
  //        return orders.map(orderListMapper::toDto);
  //    }

  @Override
  @Transactional(readOnly = true)
  public OrderDetailResponse getOrderById(UUID customerId, UUID orderId) {
    Order order = orderInternalService.findByIdAndCustomerId(orderId, customerId);

    return orderDetailMapper.toDto(order);
  }

  @Override
  public CreateOrderResponse createOrder(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      CreateOrderRequest request,
      String clientIp) {

    GetAddressByCustomerIdAndAddressIdResponse address =
        getAddressByIdAndCustomerId(customerId, request.addressId());

    List<CartItem> cartItems =
        cartGrpcClient.getCartItemsByIdsAndCustomerId(request.cartItemIds(), customerId);

    List<String> variantIds = cartItems.stream().map(CartItem::getVariantId).toList();

    Map<UUID, Variant> variantMap = fetchVariantMap(variantIds);

    OrderItemsResult itemsResult = buildOrderItems(cartItems, variantMap);

    CouponResult couponResult =
        applyCoupon(request.couponIds(), itemsResult.subtotal(), SHIPPING_FEE);

    long total =
        itemsResult.subtotal()
            + SHIPPING_FEE
            - couponResult.discountAmount()
            - couponResult.discountShippingFee();

    validateExpectedPrices(
        request,
        itemsResult.subtotal(),
        couponResult.discountAmount(),
        couponResult.discountShippingFee,
        total);

    Order savedOrder =
        transactionTemplate.execute(
            status -> {
              Order order =
                  buildAndSaveOrder(
                      accountId,
                      customerId,
                      address,
                      request,
                      itemsResult,
                      couponResult,
                      SHIPPING_FEE,
                      total);

              orderStatusHistoryInternalService.save(order, OrderStatus.PENDING);
              return order;
            });

    String paymentUrl = null;
    if (savedOrder != null && savedOrder.getPaymentMethod() != OrderPaymentMethod.COD) {
      try {
        log.info(
            "[Order] Initiating payment request for Order ID: {} via method: {}",
            savedOrder.getId(),
            savedOrder.getPaymentMethod());

        paymentUrl =
            paymentGrpcClient.createPayment(
                savedOrder.getId(),
                savedOrder.getTotal(),
                savedOrder.getPaymentMethod(),
                clientIp,
                customerId);

        log.info("[Order] Payment URL successfully generated for Order ID: {}", savedOrder.getId());
      } catch (Exception e) {
        log.error(
            "[Order] Failed to generate payment URL for Order ID: {}. Error: {}",
            savedOrder.getId(),
            e.getMessage());

        paymentUrl = null;
      }
    }

    orderProducer.publishOrderCreatedEvent(
        accountId,
        accountRoleId,
        customerId,
        null,
        savedOrder.getId(),
        savedOrder.getOrderNumber(),
        savedOrder.getOrderItems(),
        request.cartItemIds());

    return createOrderResponseMapper.toDto(savedOrder, paymentUrl);
  }

  @Override
  @Transactional
  public OrderDetailResponse updateOrder(
      UUID accountId, UUID customerId, UUID orderId, UpdateOrderRequest request) {
    Order order = orderInternalService.findByIdAndCustomerId(orderId, customerId);

    if (!OrderStatus.UPDATABLE.contains(order.getStatus())) {
      throw new AppException(ErrorCode.ORDER_CANNOT_UPDATE);
    }

    if (request.addressId() != null && !request.addressId().equals(order.getAddressId())) {

      // TODO: Implement dynamic shipping fee validation.
      // If the address changes, re-calculate the fee and ensure:
      // 1. New fee <= initial estimated fee to prevent total price bypass.
      // 2. Applied shipping coupons remain valid for the new destination.
      // Otherwise, reject the order or force a checkout refresh.

      GetAddressByCustomerIdAndAddressIdResponse address =
          getAddressByIdAndCustomerId(customerId, request.addressId());
      order.setAddressId(UuidUtils.parse(address.getId()));
      order.setCityCode(address.getCityCode());
      order.setCityName(address.getCityName());
      order.setWardCode(address.getWardCode());
      order.setWardName(address.getWardName());
      order.setAddressDetail(address.getDetail());
    }

    if (request.notes().isPresent()) {
      order.setNotes(request.notes().orElse(null));
    }

    orderRepository.save(order);

    return orderDetailMapper.toDto(order);
  }

  @Override
  public List<OrderStatusHistoryResponse> getOrderTracking(UUID customerId, UUID orderId) {
    Order order = orderInternalService.findByIdAndCustomerId(orderId, customerId);

    return orderStatusHistoryInternalService.getStatusHistories(order.getId());
  }

  @Override
  @Transactional
  public OrderDetailResponse cancelOrder(
      UUID accountId,
      UUID accountRoleId,
      UUID customerId,
      UUID orderId,
      CancelOrderRequest request) {

    Order order = orderInternalService.findByIdAndCustomerId(orderId, customerId);

    OrderStatus status = order.getStatus();

    boolean canCancel = OrderStatus.CANCELLABLE.contains(status);
    boolean canCancelRequest = OrderStatus.CANCEL_REQUESTABLE.contains(status);

    if (status == OrderStatus.CANCELLED || status == OrderStatus.CANCEL_REQUESTED) {
      throw new AppException(ErrorCode.ORDER_ALREADY_CANCELLED);
    }

    if (!canCancel && !canCancelRequest) {
      throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL);
    }

    order.setCancelReason(request.reason());
    order.setCancelNote(request.cancelNote());
    order.setCancelledAt(LocalDateTime.now());
    order.setCancelledBy(accountId);

    if (canCancelRequest) {
      order.setStatus(OrderStatus.CANCEL_REQUESTED);
      orderStatusHistoryInternalService.save(order, OrderStatus.CANCEL_REQUESTED);

      //            orderEventPublisher.publishOrderCancelRequestedEvent(
      //                    accountId,
      //                    accountRoleId,
      //                    order);
    } else if (canCancel) {

      boolean needRefund =
          status == OrderStatus.CONFIRMED && order.getPaymentMethod() != OrderPaymentMethod.COD;

      if (needRefund) {
        order.setStatus(OrderStatus.CANCELLING);
        orderStatusHistoryInternalService.save(order, OrderStatus.CANCELLING);
      } else {
        order.setStatus(OrderStatus.CANCELLED);
        orderStatusHistoryInternalService.save(order, OrderStatus.CANCELLED);
      }

      orderProducer.publishOrderCancelledEvent(
          accountId,
          accountRoleId,
          customerId,
          order.getId(),
          order.getOrderNumber(),
          status,
          needRefund,
          order.getOrderItems());
    }
    return orderDetailMapper.toDto(orderRepository.save(order));
  }

  private GetAddressByCustomerIdAndAddressIdResponse getAddressByIdAndCustomerId(
      UUID customerId, UUID addressId) {
    return customerGrpcClient.getAddressByCustomerIdAndAddressId(addressId, customerId);
  }

  private Map<UUID, Variant> fetchVariantMap(List<String> variantIds) {

    return inventoryGrpcClient.getVariantsByIds(variantIds).stream()
        .collect(Collectors.toMap(v -> UUID.fromString(v.getId()), v -> v));
  }

  private OrderItem createOrderItem(
      Variant variant, CartItem item, long unitPrice, long itemSubtotal, String displayValue) {
    return OrderItem.builder()
        .productId(UUID.fromString(variant.getProductId()))
        .productName(variant.getProductName())
        .variantId(UUID.fromString(item.getVariantId()))
        .variantName(displayValue)
        .variantImageUrl(variant.getImage())
        .quantity(item.getQuantity())
        .unitPrice(unitPrice)
        .subtotal(itemSubtotal)
        .discountAmount(0L)
        .finalAmount(itemSubtotal)
        .build();
  }

  private OrderItemsResult buildOrderItems(List<CartItem> items, Map<UUID, Variant> variantMap) {
    List<OrderItem> orderItems = new ArrayList<>();
    long subtotal = 0L;

    for (CartItem item : items) {
      Variant variant = variantMap.get(UuidUtils.parse(item.getVariantId()));
      if (variant.getStock() < item.getQuantity())
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);

      long itemSubtotal = variant.getPrice() * item.getQuantity();
      subtotal += itemSubtotal;

      String displayValue = mergeAttributeValues(variant.getAttributesList());

      orderItems.add(
          createOrderItem(variant, item, variant.getPrice(), itemSubtotal, displayValue));
    }

    return new OrderItemsResult(orderItems, subtotal);
  }

  private String mergeAttributeValues(List<AttributeValue> attributes) {
    return attributes.stream().map(AttributeValue::getValue).collect(Collectors.joining(", "));
  }

  private CouponResult applyCoupon(List<UUID> couponIds, long subtotal, long shippingFee) {
    if (couponIds == null || couponIds.isEmpty()) return new CouponResult(null, null, 0L, 0L);

    long discountAmount = 0L;
    long discountShippingFee = 0L;
    Coupon productCoupon = null;
    Coupon shippingCoupon = null;

    for (UUID couponId : couponIds) {
      Coupon coupon = couponInternalService.validateAndGetCoupon(couponId, subtotal, shippingFee);

      if (coupon.getType().equals(CouponType.FREE_SHIP)) {
        if (shippingCoupon != null) {
          throw new AppException(ErrorCode.DUPLICATE_SHIPPING_COUPON);
        }
        shippingCoupon = coupon;
        discountShippingFee =
            couponInternalService.calculateDiscount(coupon, subtotal, shippingFee);
      } else {
        if (productCoupon != null) {
          throw new AppException(ErrorCode.DUPLICATE_PRODUCT_COUPON);
        }
        productCoupon = coupon;
        discountAmount = couponInternalService.calculateDiscount(coupon, subtotal, shippingFee);
      }
    }

    return new CouponResult(productCoupon, shippingCoupon, discountAmount, discountShippingFee);
  }

  private void validateExpectedPrices(
      CreateOrderRequest request,
      long subtotal,
      long discountAmount,
      long discountShippingFee,
      long total) {
    if (!request.expectedSubtotal().equals(subtotal)
        || !request.expectedDiscountAmount().equals(discountAmount)
        || !request.expectedTotal().equals(total)
        || !request.expectedDiscountShippingFee().equals(discountShippingFee)) {
      throw new AppException(ErrorCode.ORDER_PRICE_MISMATCH);
    }
  }

  private Order buildAndSaveOrder(
      UUID accountId,
      UUID customerId,
      GetAddressByCustomerIdAndAddressIdResponse address,
      CreateOrderRequest request,
      OrderItemsResult itemsResult,
      CouponResult couponResult,
      long shippingFee,
      long total) {
    Order order =
        Order.builder()
            .createdBy(accountId)
            .customerId(customerId)
            .addressId(UuidUtils.parse(address.getId()))
            .receiverName(address.getName())
            .receiverPhone(address.getPhone())
            .cityCode(address.getCityCode())
            .cityName(address.getCityName())
            .wardCode(address.getWardCode())
            .wardName(address.getWardName())
            .addressDetail(address.getDetail())
            .paymentMethod(request.paymentMethod())
            .notes(request.notes())
            .subtotal(itemsResult.subtotal())
            .shippingFee(shippingFee)
            .discountAmount(couponResult.discountAmount())
            .discountShippingFee(couponResult.discountShippingFee())
            .total(total)
            .build();

    itemsResult.items().forEach(item -> item.setOrder(order));
    order.getOrderItems().addAll(itemsResult.items());

    if (couponResult.productCoupon() != null) {
      order
          .getOrderCoupons()
          .add(
              OrderCoupon.builder()
                  .order(order)
                  .coupon(couponResult.productCoupon())
                  .discountApplied(couponResult.discountAmount())
                  .build());
      couponInternalService.incrementUsedCount(couponResult.productCoupon().getId());
    }

    if (couponResult.shippingCoupon() != null) {
      order
          .getOrderCoupons()
          .add(
              OrderCoupon.builder()
                  .order(order)
                  .coupon(couponResult.shippingCoupon())
                  .discountApplied(couponResult.discountShippingFee())
                  .build());
      couponInternalService.incrementUsedCount(couponResult.shippingCoupon().getId());
    }

    orderRepository.save(order);
    return order;
  }

  //
  //    private Order findOrderById(UUID orderId) {
  //        return orderRepository.findByIdAndIsDeleteFalse(orderId)
  //                .orElseThrow(() -> new PermanentException(ErrorCode.ORDER_NOT_FOUND, orderId));
  //    }

  private record OrderItemsResult(List<OrderItem> items, long subtotal) {}

  private record CouponResult(
      Coupon productCoupon, Coupon shippingCoupon, long discountAmount, long discountShippingFee) {}
}
