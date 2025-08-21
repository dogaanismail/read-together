package org.readtogether.user.controller;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.ReadingPreferencesResponseFactory;
import org.readtogether.user.model.ReadingPreferencesResponse;
import org.readtogether.user.model.ReadingPreferencesUpdateRequest;
import org.readtogether.user.service.ReadingPreferencesService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReadingPreferencesResponse> getReadingPreferences(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        ReadingPreferencesEntity preferences = readingPreferencesService.getUserReadingPreferences(userId);
        return ResponseEntity.ok(ReadingPreferencesResponseFactory.createFromEntity(preferences));
    }

    @PutMapping
    public ResponseEntity<ReadingPreferencesResponse> updateReadingPreferences(
            @RequestBody ReadingPreferencesUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        ReadingPreferencesEntity updated = readingPreferencesService.updateReadingPreferences(userId, request);
        return ResponseEntity.ok(ReadingPreferencesResponseFactory.createFromEntity(updated));
    }

    @GetMapping("/playback-settings")
    public ResponseEntity<Map<String, Object>> getPlaybackSettings(Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);

        Map<String, Object> playbackSettings = Map.of(
                "speedMultiplier", readingPreferencesService.getSpeedMultiplier(userId),
                "videoQuality", readingPreferencesService.getVideoQualityResolution(userId),
                "languageCode", readingPreferencesService.getLanguageCode(userId),
                "subtitlesEnabled", readingPreferencesService.shouldEnableSubtitles(userId),
                "autoplay", readingPreferencesService.shouldAutoplay(userId)
        );

        return ResponseEntity.ok(playbackSettings);
    }
}
