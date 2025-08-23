package org.readtogether.notification.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.model.NotificationResponse;
import org.readtogether.notification.model.NotificationPreferencesResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class NotificationResponseFixtures {

    public static NotificationResponse createDefaultNotificationResponse() {
        return NotificationResponse.builder()
                .id(NotificationEntityFixtures.DEFAULT_NOTIFICATION_ID)
                .userId(NotificationEntityFixtures.DEFAULT_USER_ID)
                .sessionId(NotificationEntityFixtures.DEFAULT_SESSION_ID)
                .type("general")
                .title("Default Notification")
                .message("This is a default notification message")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .readAt(null)
                .userName("John Doe")
                .userAvatar("https://example.com/avatar.jpg")
                .sessionTitle("Sample Reading Session")
                .metadata("{\"additionalInfo\": \"test data\"}")
                .build();
    }

    public static NotificationResponse createReadNotificationResponse() {
        return NotificationResponse.builder()
                .id(UUID.randomUUID())
                .userId(NotificationEntityFixtures.DEFAULT_USER_ID)
                .sessionId(NotificationEntityFixtures.DEFAULT_SESSION_ID)
                .type("session_complete")
                .title("Session Upload Complete")
                .message("Your session has been uploaded successfully")
                .isRead(true)
                .createdAt(LocalDateTime.now().minusHours(1))
                .readAt(LocalDateTime.now().minusMinutes(30))
                .userName("Jane Smith")
                .userAvatar("https://example.com/avatar2.jpg")
                .sessionTitle("Reading Practice Session")
                .metadata("{\"sessionId\": \"12345\"}")
                .build();
    }

    public static NotificationResponse createMinimalNotificationResponse() {
        return NotificationResponse.builder()
                .id(UUID.randomUUID())
                .userId(NotificationEntityFixtures.DEFAULT_USER_ID)
                .sessionId(null)
                .type("system")
                .title("System Alert")
                .message("System maintenance scheduled")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .readAt(null)
                .userName(null)
                .userAvatar(null)
                .sessionTitle(null)
                .metadata(null)
                .build();
    }

    public static NotificationPreferencesResponse createDefaultNotificationPreferencesResponse() {
        return NotificationPreferencesResponse.builder()
                .emailNotifications(true)
                .pushNotifications(true)
                .sessionLikes(true)
                .newFollowers(true)
                .liveStreamAlerts(true)
                .weeklyDigest(true)
                .marketingEmails(false)
                .uploadStatus(true)
                .emailAddress("user@example.com")
                .phoneNumber("+1234567890")
                .pushSubscriptionEndpoint("https://push.example.com/endpoint")
                .build();
    }

    public static NotificationPreferencesResponse createMinimalNotificationPreferencesResponse() {
        return NotificationPreferencesResponse.builder()
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
                .build();
    }
}