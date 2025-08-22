package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.factory.NotificationPreferencesResponseFactory;
import org.readtogether.notification.model.NotificationPreferencesResponse;
import org.readtogether.notification.service.NotificationPreferencesService;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.PrivacySettingsResponseFactory;
import org.readtogether.user.factory.ReadingPreferencesResponseFactory;
import org.readtogether.user.model.response.AccountSettingsResponse;
import org.readtogether.user.model.request.AccountSettingsUpdateRequest;
import org.readtogether.user.model.response.PrivacySettingsResponse;
import org.readtogether.user.model.response.ReadingPreferencesResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSettingsService {

    private final PrivacySettingsService privacySettingsService;
    private final ReadingPreferencesService readingPreferencesService;
    private final NotificationPreferencesService notificationPreferencesService;

    public AccountSettingsResponse getAllSettings(
            UUID userId) {

        PrivacySettingsEntity privacySettings = privacySettingsService.getUserPrivacySettings(userId);
        ReadingPreferencesEntity readingPreferences = readingPreferencesService.getUserReadingPreferences(userId);
        NotificationPreferenceEntity notificationPreferences = notificationPreferencesService.getUserPreferences(userId);

        PrivacySettingsResponse privacyDto = PrivacySettingsResponseFactory.createFromEntity(privacySettings);
        ReadingPreferencesResponse readingDto = ReadingPreferencesResponseFactory.createFromEntity(readingPreferences);
        NotificationPreferencesResponse notifDto = NotificationPreferencesResponseFactory.createFromEntity(notificationPreferences);

        return new AccountSettingsResponse(privacyDto, readingDto, notifDto);
    }

    @Transactional
    public AccountSettingsResponse updateAllSettings(
            UUID userId,
            AccountSettingsUpdateRequest request) {

        PrivacySettingsEntity updatedPrivacy = null;
        ReadingPreferencesEntity updatedReading = null;
        NotificationPreferenceEntity updatedNotifications = null;

        if (request.getPrivacy() != null) {
            updatedPrivacy = privacySettingsService.updatePrivacySettings(userId, request.getPrivacy());
        }

        if (request.getReading() != null) {
            updatedReading = readingPreferencesService.updateReadingPreferences(userId, request.getReading());
        }

        if (request.getNotifications() != null) {
            updatedNotifications = notificationPreferencesService.updatePreferences(userId, request.getNotifications());
        }

        PrivacySettingsEntity finalPrivacy = updatedPrivacy != null ? updatedPrivacy : privacySettingsService.getUserPrivacySettings(userId);
        ReadingPreferencesEntity finalReading = updatedReading != null ? updatedReading : readingPreferencesService.getUserReadingPreferences(userId);
        NotificationPreferenceEntity finalNotif = updatedNotifications != null ? updatedNotifications : notificationPreferencesService.getUserPreferences(userId);

        return new AccountSettingsResponse(
                PrivacySettingsResponseFactory.createFromEntity(finalPrivacy),
                ReadingPreferencesResponseFactory.createFromEntity(finalReading),
                NotificationPreferencesResponseFactory.createFromEntity(finalNotif)
        );
    }
}
