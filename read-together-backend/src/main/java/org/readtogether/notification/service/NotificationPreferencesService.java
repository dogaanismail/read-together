package org.readtogether.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.entity.NotificationPreferenceType;
import org.readtogether.notification.factory.NotificationPreferencesFactory;
import org.readtogether.notification.repository.NotificationPreferencesRepository;
import org.readtogether.notification.util.NotificationPreferencesUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPreferencesService {

    private final NotificationPreferencesRepository preferencesRepository;

    @Transactional(readOnly = true)
    public NotificationPreferenceEntity getUserPreferences(UUID userId) {

        return preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
    }

    @Transactional
    public NotificationPreferenceEntity updatePreferences(
            UUID userId,
            NotificationPreferenceEntity preferences) {

        NotificationPreferenceEntity existing = preferencesRepository.findByUserId(userId)
                .orElse(NotificationPreferencesFactory.createFromUpdateRequest(userId, preferences));

        NotificationPreferencesUtils.applyUpdates(existing, preferences);
        existing.setUpdatedAt(Instant.now());

        return preferencesRepository.save(existing);
    }

    @Transactional
    public NotificationPreferenceEntity createDefaultPreferences(UUID userId) {

        if (preferencesRepository.existsByUserId(userId)) {
            return preferencesRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("User preferences not found after existence check"));
        }

        NotificationPreferenceEntity defaults = NotificationPreferencesFactory.createDefaultPreferences(userId);
        return preferencesRepository.save(defaults);
    }

    @Transactional
    public void updatePushSubscription(
            UUID userId,
            String endpoint,
            String keys) {

        NotificationPreferenceEntity preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferencesInternal(userId));

        preferences.setPushSubscriptionEndpoint(endpoint);
        preferences.setPushSubscriptionKeys(keys);
        preferences.setUpdatedAt(Instant.now());

        preferencesRepository.save(preferences);
    }

    public boolean shouldSendEmailNotification(
            UUID userId,
            NotificationPreferenceType preferenceType) {

        NotificationPreferenceEntity preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferencesInternal(userId));
        return NotificationPreferencesUtils.shouldSendEmailNotification(preferences, preferenceType);
    }

    public boolean shouldSendPushNotification(
            UUID userId,
            NotificationPreferenceType preferenceType) {

        NotificationPreferenceEntity preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferencesInternal(userId));
        return NotificationPreferencesUtils.shouldSendPushNotification(preferences, preferenceType);
    }

    private NotificationPreferenceEntity createDefaultPreferencesInternal(UUID userId) {

        if (preferencesRepository.existsByUserId(userId)) {
            return preferencesRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("User preferences not found after existence check"));
        }

        NotificationPreferenceEntity defaults = NotificationPreferencesFactory.createDefaultPreferences(userId);
        return preferencesRepository.save(defaults);
    }
}
