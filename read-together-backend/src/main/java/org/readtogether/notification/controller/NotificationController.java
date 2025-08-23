package org.readtogether.notification.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.notification.factory.NotificationResponseFactory;
import org.readtogether.notification.model.NotificationResponse;
import org.readtogether.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationEntity> notifications = notificationService.getUserNotifications(userId, pageable);

        // Convert to enhanced response model expected by frontend
        // TODO: Fetch userName and userAvatar from User service
        // TODO: Fetch sessionTitle from Session service if sessionId exists
        Page<NotificationResponse> response = notifications.map(NotificationResponseFactory::createFromEntity);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        long count = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID notificationId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        boolean updated = notificationService.markAsRead(notificationId, userId);

        return updated ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @PutMapping("/mark-all-read")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        int updatedCount = notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(Map.of("markedAsRead", updatedCount));
    }
}
