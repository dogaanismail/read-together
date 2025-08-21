package org.readtogether.notification.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationPreferenceEntity;

import java.util.UUID;

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
}
