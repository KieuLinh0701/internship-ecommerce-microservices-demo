package com.teamsolution.inventory.scheduler;

import com.teamsolution.inventory.config.properties.TempImageCleanupProperties;
import com.teamsolution.inventory.service.internal.ProductImageInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempImageCleanupScheduler {

  private static final String SCHEDULER = "TempImageCleanupScheduler";

  private final TempImageCleanupProperties properties;
  private final ProductImageInternalService productImageService;

  @Scheduled(fixedRateString = "${temp-image-cleanup.fixed-rate-ms}")
  public void cleanupTempImages() {

    log.info("[{}] START cleanupTempImages | ttlMinutes={}", SCHEDULER, properties.getTtlMinutes());

    try {
      int deleted = productImageService.cleanupTempImages(properties.getTtlMinutes());

      log.info("[{}] END cleanupTempImages | deleted={}", SCHEDULER, deleted);
    } catch (Exception e) {

      log.error("[{}] ERROR cleanupTempImages | message={}", SCHEDULER, e.getMessage());
    }
  }
}
