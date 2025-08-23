package org.readtogether.notification.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.controller.NotificationPreferencesController.PushSubscriptionRequest;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;

@UtilityClass
public class NotificationRequestFixtures {

    public static NotificationPreferencesUpdateRequest createDefaultPreferencesUpdateRequest() {

        return NotificationPreferencesUpdateRequest.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .sessionLikes(true)
                .newFollowers(true)
                .liveStreamAlerts(true)
                .weeklyDigest(true)
                .marketingEmails(false)
                .uploadStatus(true)
                .emailAddress("test@example.com")
                .phoneNumber("+1234567890")
                .pushSubscriptionEndpoint("https://push.example.com/endpoint")
                .pushSubscriptionKeys("{\"p256dh\":\"testkey\",\"auth\":\"testauth\"}")
                .build();
    }

    public static NotificationPreferencesUpdateRequest createPreferencesUpdateRequest(
            boolean emailEnabled,
            boolean pushEnabled,
            boolean sessionLikes,
            boolean newFollowers,
            boolean liveStreamAlerts,
            boolean weeklyDigest,
            boolean marketingEmails,
            boolean uploadStatus) {

        return NotificationPreferencesUpdateRequest.builder()
                .emailNotifications(emailEnabled)
                .pushNotifications(pushEnabled)
                .sessionLikes(sessionLikes)
                .newFollowers(newFollowers)
                .liveStreamAlerts(liveStreamAlerts)
                .weeklyDigest(weeklyDigest)
                .marketingEmails(marketingEmails)
                .uploadStatus(uploadStatus)
                .emailAddress(emailEnabled ? "user@example.com" : null)
                .phoneNumber(pushEnabled ? "+1234567890" : null)
                .pushSubscriptionEndpoint(pushEnabled ? "https://push.example.com/endpoint" : null)
                .pushSubscriptionKeys(pushEnabled ? "{\"p256dh\":\"key\",\"auth\":\"auth\"}" : null)
                .build();
    }

    public static PushSubscriptionRequest createPushSubscriptionRequest(
            String endpoint,
            String keys) {

        PushSubscriptionRequest request = new PushSubscriptionRequest();
        request.endpoint = endpoint;
        request.keys = keys;
        return request;
    }

    public static PushSubscriptionRequest createDefaultPushSubscriptionRequest() {

        return createPushSubscriptionRequest(
                "https://push.example.com/subscription/12345",
                "{\"p256dh\":\"BNcRdreALRFXTkOOUHK1EtK2wtaz5Ry4YfYCA_0QTpQtUbVlUls0VJXg7A8u-Ts1XbjhazAkj7I99e8QcYP7DkM\",\"auth\":\"tBHItJI5svbpez7KI4CCXg\"}"
        );
    }

    public static PushSubscriptionRequest createInvalidPushSubscriptionRequest() {

        return createPushSubscriptionRequest(
                "invalid-endpoint",
                "invalid-keys"
        );
    }
}