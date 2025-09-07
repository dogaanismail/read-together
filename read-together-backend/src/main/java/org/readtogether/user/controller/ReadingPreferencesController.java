package org.readtogether.user.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.ReadingPreferencesResponseFactory;
import org.readtogether.user.model.response.ReadingPreferencesResponse;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;
import org.readtogether.user.service.ReadingPreferencesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reading-preferences")
@RequiredArgsConstructor
public class ReadingPreferencesController {

    private final ReadingPreferencesService readingPreferencesService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<ReadingPreferencesResponse> getReadingPreferences(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        ReadingPreferencesEntity preferences = readingPreferencesService.getUserReadingPreferences(userId);

        return CustomResponse.successOf(ReadingPreferencesResponseFactory.createFromEntity(preferences));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<ReadingPreferencesResponse> updateReadingPreferences(
            @RequestBody ReadingPreferencesUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        ReadingPreferencesEntity updated = readingPreferencesService.updateReadingPreferences(userId, request);

        return CustomResponse.successOf(ReadingPreferencesResponseFactory.createFromEntity(updated));
    }

    @GetMapping("/playback-settings")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<Map<String, Object>> getPlaybackSettings(
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);

        Map<String, Object> playbackSettings = Map.of(
                "speedMultiplier", readingPreferencesService.getSpeedMultiplier(userId),
                "videoQuality", readingPreferencesService.getVideoQualityResolution(userId),
                "languageCode", readingPreferencesService.getLanguageCode(userId),
                "subtitlesEnabled", readingPreferencesService.shouldEnableSubtitles(userId),
                "autoplay", readingPreferencesService.shouldAutoplay(userId)
        );

        return CustomResponse.successOf(playbackSettings);
    }
}
