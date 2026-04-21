package com.teamsolution.order.service.customer.impl;

import com.teamsolution.order.dto.response.OrderItemFeedbackResponse;
import com.teamsolution.order.entity.OrderItemFeedback;
import com.teamsolution.order.grpc.client.CustomerGrpcClient;
import com.teamsolution.order.mapper.OrderItemFeedbackMapper;
import com.teamsolution.order.repository.OrderItemFeedbackRepository;
import com.teamsolution.order.service.customer.OrderItemFeedbackService;
import com.teamsolution.order.service.internal.OrderInternalService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemFeedbackServiceImpl implements OrderItemFeedbackService {

  private final OrderItemFeedbackRepository feedbackRepository;
  private final CustomerGrpcClient customerGrpcClient;
  private final OrderInternalService orderInternalService;
  private final OrderItemFeedbackMapper feedbackMapper;

  @Override
  @Transactional(readOnly = true)
  public List<OrderItemFeedbackResponse> getFeedbacksByOrderNumber(UUID customerId, UUID orderId) {
    orderInternalService.findByIdAndCustomerId(orderId, customerId);

    List<OrderItemFeedback> feedbacks = feedbackRepository.findByOrderId(orderId);

    String customerName = customerGrpcClient.getCustomerNameByCustomerId(customerId);

    return feedbacks.stream().map(f -> feedbackMapper.toDto(f, customerName)).toList();
  }

  //    @Override
  //    @Transactional
  //    public List<OrderItemFeedbackDto> createOrderFeedback(
  //            UUID accountId,
  //            UUID orderId,
  //            CreateOrderFeedbackRequest request) {
  //
  //        UUID customerId = customerGrpcClient.getCustomerIdByAccountId(accountId);
  //
  //        Order order = orderService.findByIdAndCustomerId(orderId, customerId);
  //
  //        validateOrderFeedback(order, request);
  //
  //        Map<UUID, OrderItem> itemMap = mapOrderItems(order);
  //
  //        List<OrderItemFeedback> feedbacks = buildFeedbacks(request, itemMap, customerId);
  //
  //        feedbackRepository.saveAll(feedbacks);
  //
  //        order.setIsFeedback(true);
  //
  //        String customerName = customerGrpcClient.getCustomerNameByCustomerId(customerId);
  //
  //        return feedbacks.stream()
  //                .map(f -> feedbackMapper.toDto(f, customerName))
  //                .toList();
  //    }

  //    @Override
  //    @Transactional
  //    public OrderItemFeedbackDto updateFeedback(
  //            UUID accountId,
  //            UUID feedbackId,
  //            UpdateFeedbackRequest request
  //    ) {
  //        UUID customerId = customerGrpcClient.getCustomerIdByAccountId(accountId);
  //
  //        OrderItemFeedback feedback = findByIdAndCustomerIdAndIsDeleteFalse(feedbackId,
  // customerId);
  //
  //        validateUpdateFeedback(feedback);
  //
  //        // Update basic info
  //        feedback.setRating(request.rating());
  //        feedback.setComment(request.comment());
  //        feedback.setIsAnonymous(Boolean.TRUE.equals(request.isAnonymous()));
  //
  //        syncImages(feedback, request.keepImageIds(), request.images());
  //
  //        feedback.setIsUpdated(true);
  //
  //
  //        String customerName = customerGrpcClient.getCustomerNameByCustomerId(customerId);
  //
  //        return feedbackMapper.toDto(feedback, customerName);
  //    }
  //
  //    @Override
  //    @Transactional
  //    public void deleteFeedback(UUID accountId, UUID feedbackId) {
  //
  //        UUID customerId = customerGrpcClient.getCustomerIdByAccountId(accountId);
  //
  //        OrderItemFeedback feedback = findByIdAndCustomerIdAndIsDeleteFalse(feedbackId,
  // customerId);
  //
  //        validateDeleteFeedback(feedback);
  //
  //        deleteImages(feedback);
  //
  //        feedback.setIsDelete(true);
  //    }
  //
  //    private void deleteImages(OrderItemFeedback feedback) {
  //        if (feedback.getImages() == null) return;
  //
  //        feedback.getImages()
  //                .forEach(image -> {
  //                    if (!Boolean.TRUE.equals(image.getIsDelete())) {
  //                        image.setIsDelete(true);
  //                    }
  //                });
  //    }
  //
  //    // Temporary workaround due to Cloudinary issues; storing image URLs directly for now
  //    private void addImages(OrderItemFeedback feedback, List<String> images) {
  //        if (images == null) return;
  //
  //        int i = 0;
  //        for (String url : images) {
  //            feedback.getImages()
  //                    .add(
  //                            OrderItemFeedbackImage.builder()
  //                                    .feedback(feedback)
  //                                    .imageUrl(url)
  //                                    .sortOrder(i++)
  //                                    .build()
  //                    );
  //        }
  //    }
  //
  //    private void syncImages(
  //            OrderItemFeedback feedback,
  //            List<UUID> keepImageIds,
  //            List<String> newImages
  //    ) {
  //        if (feedback.getImages() == null) return;
  //
  //        List<UUID> keepIds = keepImageIds != null ? keepImageIds : List.of();
  //
  //        Set<UUID> existingIds = feedback.getImages()
  //                .stream()
  //                .map(OrderItemFeedbackImage::getId)
  //                .collect(Collectors.toSet());
  //
  //        if (!existingIds.containsAll(keepIds)) {
  //            throw new AppException(ErrorCode.INVALID_FEEDBACK_IMAGE);
  //        }
  //
  //        feedback.getImages()
  //                .forEach(img -> {
  //                    if (!keepIds.contains(img.getId()) &&
  // !Boolean.TRUE.equals(img.getIsDelete())) {
  //                        img.setIsDelete(true);
  //                    }
  //                });
  //
  //        addImages(feedback, newImages);
  //    }
  //
  //    private void validateUpdateFeedback(OrderItemFeedback feedback) {
  //        if (!FeedbackPolicyUtils.canUpdate(feedback.getStatus())) {
  //            throw new AppException(ErrorCode.FEEDBACK_CANNOT_UPDATE);
  //        }
  //
  //        LocalDateTime expiredAt = feedback.getCreatedAt()
  //                .plusDays(FeedbackPolicyUtils.UPDATED_DAYS);
  //
  //        if (LocalDateTime.now()
  //                .isAfter(expiredAt)) {
  //            throw new AppException(ErrorCode.FEEDBACK_UPDATE_TIME_EXPIRED);
  //        }
  //
  //        if (feedback.getIsUpdated()) {
  //            throw new AppException(ErrorCode.FEEDBACK_ALREADY_UPDATED);
  //        }
  //    }
  //
  //    private void validateDeleteFeedback(OrderItemFeedback feedback) {
  //        if (!FeedbackPolicyUtils.canDelete(feedback.getStatus())) {
  //            throw new AppException(ErrorCode.FEEDBACK_CANNOT_DELETE);
  //        }
  //
  //        LocalDateTime expiredAt = feedback.getCreatedAt()
  //                .plusDays(FeedbackPolicyUtils.DELETE_DAYS);
  //
  //        if (LocalDateTime.now()
  //                .isAfter(expiredAt)) {
  //            throw new AppException(ErrorCode.FEEDBACK_DELETE_TIME_EXPIRED);
  //        }
  //    }
  //
  //    private void validateOrderFeedback(Order order, CreateOrderFeedbackRequest request) {
  //
  //        if (order.getStatus() != OrderStatus.COMPLETED) {
  //            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);
  //        }
  //
  //        LocalDateTime completedAt = order.getCompletedAt();
  //        if (completedAt == null) {
  //            log.error("Order {} has null completedAt", order.getOrderNumber());
  //            throw new AppException(ErrorCode.ORDER_COMPLETED_AT_NULL);
  //        }
  //
  //        LocalDateTime expiredAt = order.getCompletedAt()
  //                .plusDays(FeedbackPolicyUtils.CREATE_DAYS);
  //
  //        if (LocalDateTime.now()
  //                .isAfter(expiredAt)) {
  //            throw new AppException(ErrorCode.FEEDBACK_TIME_EXPIRED);
  //        }
  //
  //        if (Boolean.TRUE.equals(order.getIsFeedback())) {
  //            throw new AppException(ErrorCode.ORDER_ALREADY_FEEDBACK);
  //        }
  //
  //        validateOrderItems(order, request);
  //    }
  //
  //    private void validateOrderItems(Order order, CreateOrderFeedbackRequest request) {
  //
  //        Set<UUID> orderItemIds = order.getOrderItems()
  //                .stream()
  //                .map(OrderItem::getId)
  //                .collect(Collectors.toSet());
  //
  //        Set<UUID> requestItemIds = request.items()
  //                .stream()
  //                .map(CreateOrderFeedbackRequest.ItemFeedback::orderItemId)
  //                .collect(Collectors.toSet());
  //
  //        if (!orderItemIds.equals(requestItemIds)) {
  //            throw new AppException(ErrorCode.ORDER_FEEDBACK_NOT_COMPLETE);
  //        }
  //    }
  //
  //    private Map<UUID, OrderItem> mapOrderItems(Order order) {
  //        return order.getOrderItems()
  //                .stream()
  //                .collect(Collectors.toMap(OrderItem::getId, i -> i));
  //    }
  //
  //    private List<OrderItemFeedback> buildFeedbacks(
  //            CreateOrderFeedbackRequest request,
  //            Map<UUID, OrderItem> itemMap,
  //            UUID customerId
  //    ) {
  //        List<OrderItemFeedback> feedbacks = new ArrayList<>();
  //
  //        for (var itemRequest : request.items()) {
  //
  //            OrderItem item = itemMap.get(itemRequest.orderItemId());
  //            if (item == null) {
  //                throw new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND);
  //            }
  //
  //            OrderItemFeedback feedback = OrderItemFeedback.builder()
  //                    .orderItem(item)
  //                    .customerId(customerId)
  //                    .productId(item.getProductId())
  //                    .productSlug(item.getProductSlug())
  //                    .productName(item.getProductName())
  //                    .variantImageUrl(item.getVariantImageUrl())
  //                    .rating(itemRequest.rating())
  //                    .comment(itemRequest.comment())
  //                    .isAnonymous(Boolean.TRUE.equals(itemRequest.isAnonymous()))
  //                    .status(OrderItemFeedbackStatus.PENDING)
  //                    .build();
  //
  //            addImages(feedback, itemRequest.images());
  //
  //            feedbacks.add(feedback);
  //        }
  //
  //        return feedbacks;
  //    }
  //
  //    private OrderItemFeedback findByIdAndCustomerIdAndIsDeleteFalse(UUID feedbackId, UUID
  // customerId) {
  //        return feedbackRepository.findByIdAndCustomerIdAndIsDeleteFalse(feedbackId, customerId)
  //                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
  //    }
}
