package com.teamsolution.order.scheduler;

import com.teamsolution.order.entity.Order;
import com.teamsolution.order.kafka.producer.OrderProducer;
import com.teamsolution.order.service.internal.OrderInternalService;
import com.teamsolution.order.service.internal.OrderStatusHistoryInternalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {
  private final OrderInternalService orderService;
  private final OrderStatusHistoryInternalService orderStatusHistoryInternalService;
  private final OrderProducer orderProducer;

  @Scheduled(cron = "0 */1 * * * *")
  public void cancelExpiredPendingOrders() {
    log.info("Running cancel expired orders job");

    List<Order> expiredOrders = orderService.bulkCancelExpiredUnpaidOrders();

    if (expiredOrders.isEmpty()) {
      log.info("No expired orders found");
      return;
    }

    orderProducer.publishOrderPaymentTimeoutEvent(expiredOrders);
    log.info("Cancelled {} expired orders", expiredOrders.size());
  }
}
