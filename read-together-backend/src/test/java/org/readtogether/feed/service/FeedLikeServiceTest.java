package org.readtogether.feed.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.entity.FeedLikeEntity;
import org.readtogether.feed.repository.FeedLikeRepository;
import org.readtogether.feed.repository.FeedRepository;
import org.readtogether.notification.service.NotificationService;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.repository.SessionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedLikeServiceTest {

    @Mock
    private FeedLikeRepository feedLikeRepository;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FeedLikeService feedLikeService;

    private UUID feedItemId;
    private UUID userId;
    private UUID otherUserId;

    @BeforeEach
    void setUp() {
        feedItemId = UUID.randomUUID();
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
    }

    @Test
    void shouldLikeFeedItem() {
        // Given
        when(feedLikeRepository.existsByFeedItemIdAndUserId(feedItemId, userId)).thenReturn(false);
        when(feedLikeRepository.save(any(FeedLikeEntity.class))).thenReturn(new FeedLikeEntity());

        // When
        boolean result = feedLikeService.likeFeedItem(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(feedLikeRepository).save(any(FeedLikeEntity.class));
        verify(feedRepository).incrementLikeCount(feedItemId);
    }

    @Test
    void shouldNotLikeFeedItemWhenAlreadyLiked() {
        // Given
        when(feedLikeRepository.existsByFeedItemIdAndUserId(feedItemId, userId)).thenReturn(true);

        // When
        boolean result = feedLikeService.likeFeedItem(feedItemId, userId);

        // Then
        assertFalse(result);
        verify(feedLikeRepository, never()).save(any());
        verify(feedRepository, never()).incrementLikeCount(any());
    }

    @Test
    void shouldUnlikeFeedItem() {
        // Given
        FeedLikeEntity like = new FeedLikeEntity();
        when(feedLikeRepository.findByFeedItemIdAndUserId(feedItemId, userId)).thenReturn(Optional.of(like));

        // When
        boolean result = feedLikeService.unlikeFeedItem(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(feedLikeRepository).delete(like);
        verify(feedRepository).decrementLikeCount(feedItemId);
    }

    @Test
    void shouldNotUnlikeFeedItemWhenNotLiked() {
        // Given
        when(feedLikeRepository.findByFeedItemIdAndUserId(feedItemId, userId)).thenReturn(Optional.empty());

        // When
        boolean result = feedLikeService.unlikeFeedItem(feedItemId, userId);

        // Then
        assertFalse(result);
        verify(feedLikeRepository, never()).delete(any());
        verify(feedRepository, never()).decrementLikeCount(any());
    }

    @Test
    void shouldLikeFeedItemWithSessionNotification() {
        // Given
        when(feedLikeRepository.existsByFeedItemIdAndUserId(feedItemId, userId)).thenReturn(false);
        when(feedLikeRepository.save(any(FeedLikeEntity.class))).thenReturn(new FeedLikeEntity());

        FeedItemEntity feedItem = FeedItemEntity.builder()
                .id(feedItemId)
                .itemType(FeedItemEntity.FeedItemType.SESSION)
                .referenceId(UUID.randomUUID())
                .build();
        when(feedRepository.findById(feedItemId)).thenReturn(Optional.of(feedItem));

        SessionEntity session = SessionEntity.builder()
                .id(feedItem.getReferenceId())
                .userId(otherUserId)
                .title("Test Session")
                .build();
        when(sessionRepository.findById(feedItem.getReferenceId())).thenReturn(Optional.of(session));

        // When
        boolean result = feedLikeService.likeFeedItem(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(notificationService).notifySessionLiked(otherUserId, userId, session);
    }

    @Test
    void shouldNotSendNotificationForOwnContent() {
        // Given
        when(feedLikeRepository.existsByFeedItemIdAndUserId(feedItemId, userId)).thenReturn(false);
        when(feedLikeRepository.save(any(FeedLikeEntity.class))).thenReturn(new FeedLikeEntity());

        FeedItemEntity feedItem = FeedItemEntity.builder()
                .id(feedItemId)
                .itemType(FeedItemEntity.FeedItemType.SESSION)
                .referenceId(UUID.randomUUID())
                .build();
        when(feedRepository.findById(feedItemId)).thenReturn(Optional.of(feedItem));

        SessionEntity session = SessionEntity.builder()
                .id(feedItem.getReferenceId())
                .userId(userId) // Same user
                .title("Test Session")
                .build();
        when(sessionRepository.findById(feedItem.getReferenceId())).thenReturn(Optional.of(session));

        // When
        boolean result = feedLikeService.likeFeedItem(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(notificationService, never()).notifySessionLiked(any(), any(), any());
    }
}