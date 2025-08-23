package org.readtogether.notification.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.notification.common.enums.NotificationType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@UtilityClass
public class NotificationEntityFixtures {

    public static final UUID DEFAULT_NOTIFICATION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440100");
    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID DEFAULT_SESSION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440200");

    public static NotificationEntity createDefaultNotificationEntity() {

        return createNotificationEntity(
                DEFAULT_NOTIFICATION_ID,
                DEFAULT_USER_ID,
                DEFAULT_SESSION_ID,
                NotificationType.GENERAL_INFO,
                "Default Notification",
                "This is a default notification message",
                false,
                Instant.now(),
                null
        );
    }

    public static NotificationEntity createReadNotification() {

        return createNotificationEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                DEFAULT_SESSION_ID,
                NotificationType.SESSION_UPLOAD_COMPLETED,
                "Session Upload Complete",
                "Your session has been uploaded successfully",
                true,
                Instant.now().minusSeconds(3600),
                Instant.now().minus(30, ChronoUnit.MINUTES)
        );
    }

    public static NotificationEntity createUnreadNotification() {

        return createNotificationEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                DEFAULT_SESSION_ID,
                NotificationType.SESSION_UPLOAD_STARTED,
                "Session Upload Started",
                "Your session upload has begun",
                false,
                Instant.now().minusSeconds(300),
                null
        );
    }

    public static NotificationEntity createSessionLikeNotification(
            UUID userId,
            UUID sessionId) {

        return createNotificationEntity(
                UUID.randomUUID(),
                userId,
                sessionId,
                NotificationType.GENERAL_INFO,
                "Session Liked",
                "Someone liked your reading session",
                false,
                Instant.now(),
                null
        );
    }

    public static NotificationEntity createSystemAlertNotification(
            UUID userId) {

        return createNotificationEntity(
                UUID.randomUUID(),
                userId,
                null,
                NotificationType.SYSTEM_ALERT,
                "System Maintenance",
                "System will be down for maintenance on Sunday",
                false,
                Instant.now(),
                null
        );
    }

    public static NotificationEntity createNotificationEntity(
            UUID id,
            UUID userId,
            UUID sessionId,
            NotificationType type,
            String title,
            String message,
            boolean isRead,
            Instant createdAt,
            Instant readAt) {

        return NotificationEntity.builder()
                .id(id)
                .userId(userId)
                .sessionId(sessionId)
                .type(type)
                .title(title)
                .message(message)
                .isRead(isRead)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .readAt(readAt)
                .metadata("{\"additionalInfo\": \"test data\"}")
                .build();
    }
}