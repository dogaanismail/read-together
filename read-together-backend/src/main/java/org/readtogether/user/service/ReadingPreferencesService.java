package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.ReadingPreferencesFactory;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;
import org.readtogether.user.repository.ReadingPreferencesRepository;
import org.readtogether.user.utils.ReadingPreferencesUtils;
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
    public ReadingPreferencesEntity getUserReadingPreferences(
            UUID userId) {

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

    @Transactional(readOnly = true)
    public double getSpeedMultiplier(
            UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return ReadingPreferencesUtils.getSpeedMultiplier(preferences.getReadingSpeed());
    }

    @Transactional(readOnly = true)
    public String getVideoQualityResolution(
            UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return ReadingPreferencesUtils.getVideoQualityResolution(preferences.getVideoQuality());
    }

    @Transactional(readOnly = true)
    public String getLanguageCode(
            UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return ReadingPreferencesUtils.getLanguageCode(preferences.getDefaultLanguage());
    }

    @Transactional(readOnly = true)
    public boolean shouldEnableSubtitles(
            UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return preferences.isSubtitlesEnabled();
    }

    @Transactional(readOnly = true)
    public boolean shouldAutoplay(
            UUID userId) {

        ReadingPreferencesEntity preferences = readingPreferencesRepository.findByUserId(userId)
                .orElse(ReadingPreferencesFactory.createDefaultPreferences(userId));
        return preferences.isAutoplay();
    }
}
