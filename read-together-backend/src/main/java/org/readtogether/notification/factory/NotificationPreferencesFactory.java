package org.readtogether.notification.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;

import java.util.UUID;

import static java.lang.Boolean.TRUE;

@UtilityClass
public class NotificationPreferencesFactory {

    public static NotificationPreferenceEntity createDefaultPreferences(UUID userId) {

        return NotificationPreferenceEntity.builder()
                .userId(userId)
                .build();
    }

    public static NotificationPreferenceEntity createFromUpdateRequest(
            UUID userId,
            NotificationPreferenceEntity updateRequest) {

        return NotificationPreferenceEntity.builder()
                .userId(userId)
                .emailNotifications(updateRequest.isEmailNotifications())
                .pushNotifications(updateRequest.isPushNotifications())
                .sessionLikes(updateRequest.isSessionLikes())
                .newFollowers(updateRequest.isNewFollowers())
                .liveStreamAlerts(updateRequest.isLiveStreamAlerts())
                .weeklyDigest(updateRequest.isWeeklyDigest())
                .marketingEmails(updateRequest.isMarketingEmails())
                .uploadStatus(updateRequest.isUploadStatus())
                .emailAddress(updateRequest.getEmailAddress())
                .phoneNumber(updateRequest.getPhoneNumber())
                .pushSubscriptionEndpoint(updateRequest.getPushSubscriptionEndpoint())
                .pushSubscriptionKeys(updateRequest.getPushSubscriptionKeys())
                .build();
    }

    public static NotificationPreferenceEntity createFromDto(
            UUID userId,
            NotificationPreferencesUpdateRequest dto) {

        return NotificationPreferenceEntity.builder()
                .userId(userId)
                .emailNotifications(TRUE.equals(dto.getEmailNotifications()))
                .pushNotifications(TRUE.equals(dto.getPushNotifications()))
                .sessionLikes(TRUE.equals(dto.getSessionLikes()))
                .newFollowers(TRUE.equals(dto.getNewFollowers()))
                .liveStreamAlerts(TRUE.equals(dto.getLiveStreamAlerts()))
                .weeklyDigest(TRUE.equals(dto.getWeeklyDigest()))
                .marketingEmails(TRUE.equals(dto.getMarketingEmails()))
                .uploadStatus(TRUE.equals(dto.getUploadStatus()))
                .emailAddress(dto.getEmailAddress())
                .phoneNumber(dto.getPhoneNumber())
                .pushSubscriptionEndpoint(dto.getPushSubscriptionEndpoint())
                .pushSubscriptionKeys(dto.getPushSubscriptionKeys())
                .build();
    }
}
