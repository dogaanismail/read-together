package org.readtogether.notification.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationPreferenceEntity;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class NotificationPreferenceEntityFixtures {

    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID SECONDARY_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    public static NotificationPreferenceEntity createDefaultPreferencesEntity() {

        return createDefaultPreferencesEntity(DEFAULT_USER_ID);
    }

    public static NotificationPreferenceEntity createDefaultPreferencesEntity(
            UUID userId) {

        return NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
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
                .pushSubscriptionKeys("{\"p256dh\":\"key123\",\"auth\":\"auth123\"}")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static NotificationPreferenceEntity createAllDisabledPreferences(
            UUID userId) {

        return NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
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
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static NotificationPreferenceEntity createMinimalPreferences(
            UUID userId) {

        return NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .emailNotifications(true)
                .pushNotifications(false)
                .sessionLikes(true)
                .newFollowers(false)
                .liveStreamAlerts(false)
                .weeklyDigest(false)
                .marketingEmails(false)
                .uploadStatus(true)
                .emailAddress("minimal@example.com")
                .phoneNumber(null)
                .pushSubscriptionEndpoint(null)
                .pushSubscriptionKeys(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static NotificationPreferenceEntity createPreferencesEntity(
            UUID userId,
            boolean emailEnabled,
            boolean pushEnabled,
            boolean sessionLikes,
            boolean newFollowers,
            boolean liveStreamAlerts,
            boolean weeklyDigest,
            boolean marketingEmails,
            boolean uploadStatus,
            String emailAddress,
            String phoneNumber,
            String pushEndpoint,
            String pushKeys) {

        return NotificationPreferenceEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .emailNotifications(emailEnabled)
                .pushNotifications(pushEnabled)
                .sessionLikes(sessionLikes)
                .newFollowers(newFollowers)
                .liveStreamAlerts(liveStreamAlerts)
                .weeklyDigest(weeklyDigest)
                .marketingEmails(marketingEmails)
                .uploadStatus(uploadStatus)
                .emailAddress(emailAddress)
                .phoneNumber(phoneNumber)
                .pushSubscriptionEndpoint(pushEndpoint)
                .pushSubscriptionKeys(pushKeys)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}