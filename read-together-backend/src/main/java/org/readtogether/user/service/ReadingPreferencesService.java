package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.ReadingPreferencesFactory;
import org.readtogether.user.model.ReadingPreferencesUpdateRequest;
import org.readtogether.user.repository.ReadingPreferencesRepository;
import org.readtogether.user.util.ReadingPreferencesUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingPreferencesService {

    private final ReadingPreferencesRepository readingPreferencesRepository;

    @Transactional(readOnly = true)
    public ReadingPreferencesEntity getUserReadingPreferences(UUID userId) {

        return readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
    }

    @Transactional
    public ReadingPreferencesEntity updateReadingPreferences(
            UUID userId,
            ReadingPreferencesUpdateRequest request) {

        ReadingPreferencesEntity existing = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createFromDto(userId, request));

        ReadingPreferencesEntity updates = ReadingPreferencesFactory.createFromDto(userId, request);
        ReadingPreferencesUtils.applyUpdates(existing, updates);
        existing.setUpdatedAt(Instant.now());

        return readingPreferencesRepository.save(existing);
    }

    public double getSpeedMultiplier(UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return ReadingPreferencesUtils.getSpeedMultiplier(preferences.getReadingSpeed());
    }

    public String getVideoQualityResolution(UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return ReadingPreferencesUtils.getVideoQualityResolution(preferences.getVideoQuality());
    }

    public String getLanguageCode(UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return ReadingPreferencesUtils.getLanguageCode(preferences.getDefaultLanguage());
    }

    public boolean shouldEnableSubtitles(UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return preferences.isSubtitlesEnabled();
    }

    public boolean shouldAutoplay(UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return preferences.isAutoplay();
    }
}
