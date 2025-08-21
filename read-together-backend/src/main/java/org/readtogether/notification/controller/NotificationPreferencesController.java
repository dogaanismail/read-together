package org.readtogether.notification.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.service.NotificationPreferencesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferencesController {

    private final NotificationPreferencesService preferencesService;

    @GetMapping
    public ResponseEntity<NotificationPreferenceEntity> getPreferences(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        NotificationPreferenceEntity preferences = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping
    public ResponseEntity<NotificationPreferenceEntity> updatePreferences(
            @RequestBody NotificationPreferenceEntity preferences,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        NotificationPreferenceEntity updated = preferencesService.updatePreferences(userId, preferences);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/push-subscription")
    public ResponseEntity<Void> updatePushSubscription(
            @RequestBody PushSubscriptionRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        preferencesService.updatePushSubscription(userId, request.endpoint, request.keys);
        return ResponseEntity.ok().build();
    }

    public static class PushSubscriptionRequest {
        public String endpoint;
        public String keys;
    }
}
