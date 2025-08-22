package org.readtogether.notification.utils;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.common.enums.NotificationPreferenceType;

@UtilityClass
public class NotificationPreferencesUtils {

    public static void applyUpdates(
            NotificationPreferenceEntity existing,
            NotificationPreferenceEntity updates) {

        existing.setEmailNotifications(updates.isEmailNotifications());
        existing.setPushNotifications(updates.isPushNotifications());
        existing.setSessionLikes(updates.isSessionLikes());
        existing.setNewFollowers(updates.isNewFollowers());
        existing.setLiveStreamAlerts(updates.isLiveStreamAlerts());
        existing.setWeeklyDigest(updates.isWeeklyDigest());
        existing.setMarketingEmails(updates.isMarketingEmails());
        existing.setUploadStatus(updates.isUploadStatus());

        if (updates.getEmailAddress() != null) {
            existing.setEmailAddress(updates.getEmailAddress());
        }

        if (updates.getPhoneNumber() != null) {
            existing.setPhoneNumber(updates.getPhoneNumber());
        }

        if (updates.getPushSubscriptionEndpoint() != null) {
            existing.setPushSubscriptionEndpoint(updates.getPushSubscriptionEndpoint());
        }

        if (updates.getPushSubscriptionKeys() != null) {
            existing.setPushSubscriptionKeys(updates.getPushSubscriptionKeys());
        }
    }

    public static boolean shouldSendEmailNotification(
            NotificationPreferenceEntity preferences,
            NotificationPreferenceType preferenceType) {

        if (!preferences.isEmailNotifications()) {
            return false;
        }

        return switch (preferenceType) {
            case SESSION_LIKES, SESSION_COMMENTS -> preferences.isSessionLikes();
            case NEW_FOLLOWERS -> preferences.isNewFollowers();
            case LIVE_STREAM_ALERTS -> preferences.isLiveStreamAlerts();
            case WEEKLY_DIGEST -> preferences.isWeeklyDigest();
            case MARKETING_EMAILS -> preferences.isMarketingEmails();
            case UPLOAD_STATUS -> preferences.isUploadStatus();
        };
    }

    public static boolean shouldSendPushNotification(
            NotificationPreferenceEntity preferences,
            NotificationPreferenceType preferenceType) {

        if (!preferences.isPushNotifications()) {
            return false;
        }

        return switch (preferenceType) {
            case SESSION_LIKES, SESSION_COMMENTS -> preferences.isSessionLikes();
            case NEW_FOLLOWERS -> preferences.isNewFollowers();
            case LIVE_STREAM_ALERTS -> preferences.isLiveStreamAlerts();
            case UPLOAD_STATUS -> preferences.isUploadStatus();
            case WEEKLY_DIGEST, MARKETING_EMAILS -> false;
        };
    }
}
