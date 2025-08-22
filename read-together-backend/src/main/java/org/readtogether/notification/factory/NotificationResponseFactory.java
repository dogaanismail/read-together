package org.readtogether.notification.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.notification.entity.enums.NotificationType;
import org.readtogether.notification.model.NotificationResponse;

import java.time.LocalDateTime;

@UtilityClass
public class NotificationResponseFactory {

    public static NotificationResponse createFromEntity(
            NotificationEntity entity,
            String userName,
            String userAvatar,
            String sessionTitle) {

        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sessionId(entity.getSessionId())
                .type(mapNotificationTypeToFrontend(entity.getType()))
                .title(entity.getTitle())
                .message(entity.getMessage())
                .isRead(entity.isRead())
                .createdAt(LocalDateTime.from(entity.getCreatedAt()))
                .readAt(entity.getReadAt())
                .userName(userName)
                .userAvatar(userAvatar)
                .sessionTitle(sessionTitle)
                .metadata(entity.getMetadata())
                .build();
    }

    public static NotificationResponse createFromEntity(NotificationEntity entity) {

        return createFromEntity(entity, null, null, null);
    }

    private static String mapNotificationTypeToFrontend(NotificationType type) {

        return switch (type) {
            case SESSION_UPLOAD_STARTED, SESSION_PROCESSING_STARTED -> "session_start";
            case SESSION_UPLOAD_COMPLETED, SESSION_PROCESSING_COMPLETED -> "session_complete";
            case SESSION_UPLOAD_FAILED, SESSION_PROCESSING_FAILED -> "session_failed";
            case GENERAL_INFO -> "like"; // Default mapping for likes
            case SYSTEM_ALERT -> "system";
            default -> "general";
        };
    }
}
