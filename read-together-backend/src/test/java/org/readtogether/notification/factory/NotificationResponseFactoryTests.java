package org.readtogether.notification.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.notification.model.NotificationResponse;
import org.readtogether.notification.fixtures.NotificationEntityFixtures;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationResponseFactory Tests")
class NotificationResponseFactoryTests {

    @Test
    @DisplayName("Should map entity to response with all fields")
    void shouldMapEntityToResponseWithAllFields() {
        // Given
        NotificationEntity entity = NotificationEntityFixtures.createDefaultNotificationEntity();
        String userName = "John Doe";
        String userAvatar = "https://example.com/avatar.jpg";
        String sessionTitle = "Sample Reading Session";

        // When
        NotificationResponse result = NotificationResponseFactory.createFromEntity(
                entity, userName, userAvatar, sessionTitle);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getUserId()).isEqualTo(entity.getUserId());
        assertThat(result.getSessionId()).isEqualTo(entity.getSessionId());
        assertThat(result.getType()).isEqualTo("like"); // GENERAL_INFO maps to "like"
        assertThat(result.getTitle()).isEqualTo(entity.getTitle());
        assertThat(result.getMessage()).isEqualTo(entity.getMessage());
        assertThat(result.isRead()).isEqualTo(entity.isRead());
        assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        assertThat(result.getReadAt()).isEqualTo(entity.getReadAt());
        assertThat(result.getUserName()).isEqualTo(userName);
        assertThat(result.getUserAvatar()).isEqualTo(userAvatar);
        assertThat(result.getSessionTitle()).isEqualTo(sessionTitle);
        assertThat(result.getMetadata()).isEqualTo(entity.getMetadata());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        // Given
        NotificationEntity entity = NotificationEntityFixtures.createSystemAlertNotification(
                NotificationEntityFixtures.DEFAULT_USER_ID);

        // When
        NotificationResponse result = NotificationResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getUserId()).isEqualTo(entity.getUserId());
        assertThat(result.getSessionId()).isNull();
        assertThat(result.getType()).isEqualTo("system"); // SYSTEM_ALERT maps to "system"
        assertThat(result.getTitle()).isEqualTo(entity.getTitle());
        assertThat(result.getMessage()).isEqualTo(entity.getMessage());
        assertThat(result.isRead()).isEqualTo(entity.isRead());
        assertThat(result.getUserName()).isNull();
        assertThat(result.getUserAvatar()).isNull();
        assertThat(result.getSessionTitle()).isNull();
    }

    @Test
    @DisplayName("Should map read notification correctly")
    void shouldMapReadNotificationCorrectly() {
        // Given
        NotificationEntity entity = NotificationEntityFixtures.createReadNotification();

        // When
        NotificationResponse result = NotificationResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isRead()).isTrue();
        assertThat(result.getReadAt()).isEqualTo(entity.getReadAt());
        assertThat(result.getType()).isEqualTo("session_complete"); // SESSION_UPLOAD_COMPLETED maps to "session_complete"
    }

    @Test
    @DisplayName("Should map unread notification correctly")
    void shouldMapUnreadNotificationCorrectly() {
        // Given
        NotificationEntity entity = NotificationEntityFixtures.createUnreadNotification();

        // When
        NotificationResponse result = NotificationResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isRead()).isFalse();
        assertThat(result.getReadAt()).isNull();
        assertThat(result.getType()).isEqualTo("session_start"); // SESSION_UPLOAD_STARTED maps to "session_start"
    }

    @Test
    @DisplayName("Should handle different notification types correctly")
    void shouldHandleDifferentNotificationTypesCorrectly() {
        // Given - Session like notification (GENERAL_INFO)
        NotificationEntity likeEntity = NotificationEntityFixtures.createSessionLikeNotification(
                NotificationEntityFixtures.DEFAULT_USER_ID,
                NotificationEntityFixtures.DEFAULT_SESSION_ID
        );

        // When
        NotificationResponse likeResult = NotificationResponseFactory.createFromEntity(likeEntity);

        // Then
        assertThat(likeResult.getType()).isEqualTo("like"); // GENERAL_INFO maps to "like"

        // Given - System alert notification
        NotificationEntity systemEntity = NotificationEntityFixtures.createSystemAlertNotification(
                NotificationEntityFixtures.DEFAULT_USER_ID
        );

        // When
        NotificationResponse systemResult = NotificationResponseFactory.createFromEntity(systemEntity);

        // Then
        assertThat(systemResult.getType()).isEqualTo("system"); // SYSTEM_ALERT maps to "system"
    }
}