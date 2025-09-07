package org.readtogether.user.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.user.model.response.AccountSettingsResponse;
import org.readtogether.user.model.request.AccountSettingsUpdateRequest;
import org.readtogether.user.service.AccountSettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-settings")
@RequiredArgsConstructor
public class AccountSettingsController {

    private final AccountSettingsService accountSettingsService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<AccountSettingsResponse> getAllSettings(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        AccountSettingsResponse settings = accountSettingsService.getAllSettings(userId);

        return CustomResponse.successOf(settings);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<AccountSettingsResponse> updateAllSettings(
            @RequestBody AccountSettingsUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        AccountSettingsResponse updated = accountSettingsService.updateAllSettings(userId, request);

        return CustomResponse.successOf(updated);
    }
}
