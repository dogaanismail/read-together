package org.readtogether.notification.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.factory.NotificationPreferencesResponseFactory;
import org.readtogether.notification.model.NotificationPreferencesResponse;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;
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
    public ResponseEntity<NotificationPreferencesResponse> getPreferences(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        NotificationPreferenceEntity preferences = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(NotificationPreferencesResponseFactory.createFromEntity(preferences));
    }

    @PutMapping
    public ResponseEntity<NotificationPreferencesResponse> updatePreferences(
            @RequestBody NotificationPreferencesUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        NotificationPreferenceEntity updated = preferencesService.updatePreferences(userId, request);
        return ResponseEntity.ok(NotificationPreferencesResponseFactory.createFromEntity(updated));
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
