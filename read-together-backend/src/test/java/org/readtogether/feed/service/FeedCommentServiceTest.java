package org.readtogether.feed.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.feed.entity.FeedCommentEntity;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.model.CreateCommentRequest;
import org.readtogether.feed.model.FeedCommentResponse;
import org.readtogether.feed.repository.FeedCommentRepository;
import org.readtogether.feed.repository.FeedRepository;
import org.readtogether.notification.service.NotificationService;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.repository.SessionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.readtogether.feed.common.enums.FeedItemType.SESSION;

@ExtendWith(MockitoExtension.class)
class FeedCommentServiceTest {

    @Mock
    private FeedCommentRepository feedCommentRepository;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FeedCommentService feedCommentService;

    private UUID feedItemId;
    private UUID userId;
    private UUID otherUserId;
    private CreateCommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        feedItemId = UUID.randomUUID();
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        
        commentRequest = new CreateCommentRequest();
        commentRequest.setContent("This is a test comment");
    }

    @Test
    void shouldCreateComment() {
        // Given
        FeedCommentEntity savedComment = FeedCommentEntity.builder()
                .id(UUID.randomUUID())
                .feedItemId(feedItemId)
                .userId(userId)
                .content(commentRequest.getContent())
                .createdAt(java.time.Instant.now())
                .build();
        
        when(feedCommentRepository.save(any(FeedCommentEntity.class))).thenReturn(savedComment);

        // When
        FeedCommentResponse result = feedCommentService.createComment(feedItemId, userId, commentRequest);

        // Then
        assertNotNull(result);
        assertEquals(commentRequest.getContent(), result.getContent());
        assertEquals(feedItemId, result.getFeedItemId());
        assertEquals(userId, result.getUserId());
        
        verify(feedCommentRepository).save(any(FeedCommentEntity.class));
        verify(feedRepository).incrementCommentCount(feedItemId);
    }

    @Test
    void shouldCreateCommentWithSessionNotification() {
        // Given
        FeedCommentEntity savedComment = FeedCommentEntity.builder()
                .id(UUID.randomUUID())
                .feedItemId(feedItemId)
                .userId(userId)
                .content(commentRequest.getContent())
                .createdAt(java.time.Instant.now())
                .build();
        
        when(feedCommentRepository.save(any(FeedCommentEntity.class))).thenReturn(savedComment);

        FeedItemEntity feedItem = FeedItemEntity.builder()
                .id(feedItemId)
                .itemType(SESSION)
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
        FeedCommentResponse result = feedCommentService.createComment(feedItemId, userId, commentRequest);

        // Then
        assertNotNull(result);
        verify(notificationService).notifySessionCommented(otherUserId, userId, session, commentRequest.getContent());
    }

    @Test
    void shouldNotSendNotificationForOwnContent() {
        // Given
        FeedCommentEntity savedComment = FeedCommentEntity.builder()
                .id(UUID.randomUUID())
                .feedItemId(feedItemId)
                .userId(userId)
                .content(commentRequest.getContent())
                .createdAt(java.time.Instant.now())
                .build();
        
        when(feedCommentRepository.save(any(FeedCommentEntity.class))).thenReturn(savedComment);

        FeedItemEntity feedItem = FeedItemEntity.builder()
                .id(feedItemId)
                .itemType(SESSION)
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
        FeedCommentResponse result = feedCommentService.createComment(feedItemId, userId, commentRequest);

        // Then
        assertNotNull(result);
        verify(notificationService, never()).notifySessionCommented(any(), any(), any(), any());
    }

    @Test
    void shouldDeleteComment() {
        // Given
        UUID commentId = UUID.randomUUID();
        FeedCommentEntity comment = FeedCommentEntity.builder()
                .id(commentId)
                .feedItemId(feedItemId)
                .userId(userId)
                .content("Test comment")
                .isDeleted(false)
                .build();
        
        when(feedCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(feedCommentRepository.save(any(FeedCommentEntity.class))).thenReturn(comment);

        // When
        boolean result = feedCommentService.deleteComment(commentId, userId);

        // Then
        assertTrue(result);
        assertTrue(comment.isDeleted());
        verify(feedCommentRepository).save(comment);
        verify(feedRepository).decrementCommentCount(feedItemId);
    }

    @Test
    void shouldNotDeleteCommentWhenNotOwner() {
        // Given
        UUID commentId = UUID.randomUUID();
        FeedCommentEntity comment = FeedCommentEntity.builder()
                .id(commentId)
                .feedItemId(feedItemId)
                .userId(otherUserId) // Different user
                .content("Test comment")
                .isDeleted(false)
                .build();
        
        when(feedCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When
        boolean result = feedCommentService.deleteComment(commentId, userId);

        // Then
        assertFalse(result);
        assertFalse(comment.isDeleted());
        verify(feedCommentRepository, never()).save(any());
        verify(feedRepository, never()).decrementCommentCount(any());
    }

    @Test
    void shouldNotDeleteCommentWhenNotFound() {
        // Given
        UUID commentId = UUID.randomUUID();
        when(feedCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When
        boolean result = feedCommentService.deleteComment(commentId, userId);

        // Then
        assertFalse(result);
        verify(feedCommentRepository, never()).save(any());
        verify(feedRepository, never()).decrementCommentCount(any());
    }
}