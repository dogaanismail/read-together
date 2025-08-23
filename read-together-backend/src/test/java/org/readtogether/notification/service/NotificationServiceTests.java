package org.readtogether.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.notification.repository.NotificationRepository;
import org.readtogether.notification.fixtures.NotificationEntityFixtures;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should get user notifications paged")
    void shouldGetUserNotificationsPaged() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<NotificationEntity> notifications = Arrays.asList(
                NotificationEntityFixtures.createDefaultNotificationEntity(),
                NotificationEntityFixtures.createReadNotification(),
                NotificationEntityFixtures.createUnreadNotification()
        );
        Page<NotificationEntity> expectedPage = new PageImpl<>(notifications, pageable, notifications.size());
        
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(expectedPage);

        // When
        Page<NotificationEntity> result = notificationService.getUserNotifications(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getPageable()).isEqualTo(pageable);
        
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Test
    @DisplayName("Should get unread count")
    void shouldGetUnreadCount() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        long expectedCount = 5L;
        
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(expectedCount);

        // When
        long result = notificationService.getUnreadCount(userId);

        // Then
        assertThat(result).isEqualTo(expectedCount);
        verify(notificationRepository).countUnreadByUserId(userId);
    }

    @Test
    @DisplayName("Should mark as read when user owns notification")
    void shouldMarkAsReadWhenOwner() {
        // Given
        UUID notificationId = UUID.randomUUID();
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        
        when(notificationRepository.markAsRead(eq(notificationId), eq(userId), any(Instant.class)))
                .thenReturn(1); // 1 row updated

        // When
        boolean result = notificationService.markAsRead(notificationId, userId);

        // Then
        assertThat(result).isTrue();
        verify(notificationRepository).markAsRead(eq(notificationId), eq(userId), any(Instant.class));
    }

    @Test
    @DisplayName("Should return false when mark as read not owner or missing")
    void shouldReturnFalseWhenMarkAsReadNotOwnerOrMissing() {
        // Given
        UUID notificationId = UUID.randomUUID();
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        
        when(notificationRepository.markAsRead(eq(notificationId), eq(userId), any(Instant.class)))
                .thenReturn(0); // 0 rows updated (notification not found or not owned by the user)

        // When
        boolean result = notificationService.markAsRead(notificationId, userId);

        // Then
        assertThat(result).isFalse();
        verify(notificationRepository).markAsRead(eq(notificationId), eq(userId), any(Instant.class));
    }

    @Test
    @DisplayName("Should mark all as read")
    void shouldMarkAllAsRead() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        int expectedUpdatedCount = 3;
        
        when(notificationRepository.markAllAsRead(eq(userId), any(Instant.class)))
                .thenReturn(expectedUpdatedCount);

        // When
        int result = notificationService.markAllAsRead(userId);

        // Then
        assertThat(result).isEqualTo(expectedUpdatedCount);
        verify(notificationRepository).markAllAsRead(eq(userId), any(Instant.class));
    }

    @Test
    @DisplayName("Should return zero when mark all as read has no unread notifications")
    void shouldReturnZeroWhenMarkAllAsReadHasNoUnreadNotifications() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        
        when(notificationRepository.markAllAsRead(eq(userId), any(Instant.class)))
                .thenReturn(0); // No unread notifications to mark

        // When
        int result = notificationService.markAllAsRead(userId);

        // Then
        assertThat(result).isEqualTo(0);
        verify(notificationRepository).markAllAsRead(eq(userId), any(Instant.class));
    }

    @Test
    @DisplayName("Should handle empty notifications page")
    void shouldHandleEmptyNotificationsPage() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificationEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(emptyPage);

        // When
        Page<NotificationEntity> result = notificationService.getUserNotifications(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Test
    @DisplayName("Should get unread count zero when no unread notifications")
    void shouldGetUnreadCountZeroWhenNoUnreadNotifications() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(0L);

        // When
        long result = notificationService.getUnreadCount(userId);

        // Then
        assertThat(result).isEqualTo(0L);
        verify(notificationRepository).countUnreadByUserId(userId);
    }

    @Test
    @DisplayName("Should handle different pagination parameters")
    void shouldHandleDifferentPaginationParameters() {
        // Given
        UUID userId = NotificationEntityFixtures.DEFAULT_USER_ID;
        Pageable customPageable = PageRequest.of(1, 5); // Page 1, size 5
        
        List<NotificationEntity> notifications = Arrays.asList(
                NotificationEntityFixtures.createDefaultNotificationEntity(),
                NotificationEntityFixtures.createReadNotification()
        );
        Page<NotificationEntity> expectedPage = new PageImpl<>(notifications, customPageable, 10); // Total 10 items
        
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, customPageable))
                .thenReturn(expectedPage);

        // When
        Page<NotificationEntity> result = notificationService.getUserNotifications(userId, customPageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(1); // Current page
        assertThat(result.getSize()).isEqualTo(5); // Page size
        
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId, customPageable);
    }
}