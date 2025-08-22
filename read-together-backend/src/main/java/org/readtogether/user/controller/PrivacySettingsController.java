package org.readtogether.user.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.factory.PrivacySettingsResponseFactory;
import org.readtogether.user.model.response.PrivacySettingsResponse;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;
import org.readtogether.user.service.PrivacySettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/privacy-settings")
@RequiredArgsConstructor
public class PrivacySettingsController {

    private final PrivacySettingsService privacySettingsService;

    @GetMapping
    public ResponseEntity<PrivacySettingsResponse> getPrivacySettings(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        PrivacySettingsEntity settings = privacySettingsService.getUserPrivacySettings(userId);
        return ResponseEntity.ok(PrivacySettingsResponseFactory.createFromEntity(settings));
    }

    @PutMapping
    public ResponseEntity<PrivacySettingsResponse> updatePrivacySettings(
            @RequestBody PrivacySettingsUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        PrivacySettingsEntity updated = privacySettingsService.updatePrivacySettings(userId, request);
        return ResponseEntity.ok(PrivacySettingsResponseFactory.createFromEntity(updated));
    }

    @GetMapping("/can-access-profile")
    public ResponseEntity<Boolean> canAccessProfile(
            @RequestParam UUID targetUserId,
            @RequestParam(defaultValue = "false") boolean isFollowing,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);
        boolean canAccess = privacySettingsService.canAccessProfile(targetUserId, currentUserId, isFollowing);
        return ResponseEntity.ok(canAccess);
    }

    @GetMapping("/can-send-message")
    public ResponseEntity<Boolean> canSendMessage(
            @RequestParam UUID targetUserId,
            @RequestParam(defaultValue = "false") boolean isFollowing,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);
        boolean canSend = privacySettingsService.canSendMessage(targetUserId, currentUserId, isFollowing);
        return ResponseEntity.ok(canSend);
    }
}
