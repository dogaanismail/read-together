package org.readtogether.user.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.user.model.response.AccountSettingsResponse;
import org.readtogether.user.model.request.AccountSettingsUpdateRequest;
import org.readtogether.user.service.AccountSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-settings")
@RequiredArgsConstructor
public class AccountSettingsController {

    private final AccountSettingsService accountSettingsService;

    @GetMapping
    public ResponseEntity<AccountSettingsResponse> getAllSettings(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        AccountSettingsResponse settings = accountSettingsService.getAllSettings(userId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<AccountSettingsResponse> updateAllSettings(
            @RequestBody AccountSettingsUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        AccountSettingsResponse updated = accountSettingsService.updateAllSettings(userId, request);
        return ResponseEntity.ok(updated);
    }
}
