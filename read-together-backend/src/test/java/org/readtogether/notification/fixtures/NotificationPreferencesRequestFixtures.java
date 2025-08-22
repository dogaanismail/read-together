package org.readtogether.notification.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;

@UtilityClass
public class NotificationPreferencesRequestFixtures {

    public static NotificationPreferencesUpdateRequest createMarketingAndDigestOff() {

        return NotificationPreferencesUpdateRequest.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .sessionLikes(true)
                .newFollowers(false)
                .liveStreamAlerts(false)
                .weeklyDigest(false)
                .marketingEmails(false)
                .uploadStatus(true)
                .emailAddress("notifications@example.com")
                .phoneNumber("+1000000000")
                .pushSubscriptionEndpoint("https://push.example.com/sub/123")
                .pushSubscriptionKeys("{\"p256dh\":\"key\",\"auth\":\"auth\"}")
                .build();
    }

    public static NotificationPreferencesUpdateRequest createAllOff() {

        return NotificationPreferencesUpdateRequest.builder()
                .emailNotifications(false)
                .pushNotifications(false)
                .sessionLikes(false)
                .newFollowers(false)
                .liveStreamAlerts(false)
                .weeklyDigest(false)
                .marketingEmails(false)
                .uploadStatus(false)
                .emailAddress(null)
                .phoneNumber(null)
                .pushSubscriptionEndpoint(null)
                .pushSubscriptionKeys(null)
                .build();
    }
}

