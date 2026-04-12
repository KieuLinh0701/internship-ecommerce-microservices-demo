package com.teamsolution.notification.controller;

import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.security.SecurityUtils;
import com.teamsolution.common.jpa.mapper.PageMapper;
import com.teamsolution.common.jpa.utils.PageableUtils;
import com.teamsolution.notification.dto.request.NotificationFilterRequest;
import com.teamsolution.notification.dto.response.NotificationResponse;
import com.teamsolution.notification.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
      NotificationFilterRequest filterRequest) {

    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();

    Pageable pageable =
        PageableUtils.toPageable(
            filterRequest.getPage(),
            filterRequest.getSize(),
            filterRequest.getSortBy(),
            filterRequest.getDirection(),
            true);

    Page<NotificationResponse> result =
        notificationService.getNotifications(accountId, accountRoleId, filterRequest, pageable);

    return ResponseEntity.ok(ApiResponse.success(PageMapper.toPageResponse(result)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<NotificationResponse>> getDetail(@PathVariable UUID id) {
    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();

    NotificationResponse dto = notificationService.getDetail(id, accountId, accountRoleId);
    return ResponseEntity.ok(ApiResponse.success(dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();

    notificationService.delete(id, accountId, accountRoleId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/{id}/read")
  public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();

    notificationService.markAsRead(id, accountId, accountRoleId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PatchMapping("/read-all")
  public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
    UUID accountId = SecurityUtils.getCurrentAccountId();
    UUID accountRoleId = SecurityUtils.getCurrentAccountRoleId();
    notificationService.markAllAsRead(accountId, accountRoleId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
