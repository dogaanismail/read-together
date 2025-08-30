package org.readtogether.notification.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.model.NotificationPreferencesResponse;

@UtilityClass
public class NotificationPreferencesResponseFactory {

    public static NotificationPreferencesResponse createFromEntity(
            NotificationPreferenceEntity entity) {

        return NotificationPreferencesResponse.builder()
                .emailNotifications(entity.isEmailNotifications())
                .pushNotifications(entity.isPushNotifications())
                .sessionLikes(entity.isSessionLikes())
                .newFollowers(entity.isNewFollowers())
                .liveStreamAlerts(entity.isLiveStreamAlerts())
                .weeklyDigest(entity.isWeeklyDigest())
                .marketingEmails(entity.isMarketingEmails())
                .uploadStatus(entity.isUploadStatus())
                .emailAddress(entity.getEmailAddress())
                .phoneNumber(entity.getPhoneNumber())
                .pushSubscriptionEndpoint(entity.getPushSubscriptionEndpoint())
                .build();
    }
}
