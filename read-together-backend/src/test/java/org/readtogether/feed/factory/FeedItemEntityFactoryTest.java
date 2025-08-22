package org.readtogether.feed.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.common.enums.MediaType;
import org.readtogether.session.common.enums.ProcessingStatus;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.feed.common.enums.FeedItemType.*;

@DisplayName("FeedItemEntityFactory Tests")
class FeedItemEntityFactoryTest {

    @Test
    @DisplayName("Should create feed item from session entity")
    void shouldCreateFeedItemFromSession() {
        // Given
        SessionEntity session = createTestSessionEntity();

        // When
        FeedItemEntity feedItem = FeedItemEntityFactory.createFromSession(session);

        // Then
        assertThat(feedItem).isNotNull();
        assertThat(feedItem.getUserId()).isEqualTo(session.getUserId());
        assertThat(feedItem.getItemType()).isEqualTo(SESSION);
        assertThat(feedItem.getReferenceId()).isEqualTo(session.getId());
        assertThat(feedItem.getTitle()).isEqualTo(session.getTitle());
        assertThat(feedItem.getDescription()).isEqualTo(session.getDescription());
        assertThat(feedItem.getMediaUrl()).isEqualTo(session.getMediaUrl());
        assertThat(feedItem.isPublic()).isEqualTo(session.isPublic());
        assertThat(feedItem.getMetadata()).contains("mediaType");
        assertThat(feedItem.getMetadata()).contains("duration");
        assertThat(feedItem.getMetadata()).contains("fileSize");
    }

    @Test
    @DisplayName("Should create complete feed item from session entity")
    void shouldCreateCompleteFeedItemFromSession() {
        // Given
        SessionEntity session = createTestSessionEntity();

        // When
        FeedItemEntity feedItem = FeedItemEntityFactory.createFeedItemFromSession(session);

        // Then
        assertThat(feedItem).isNotNull();
        assertThat(feedItem.getUserId()).isEqualTo(session.getUserId());
        assertThat(feedItem.getItemType()).isEqualTo(SESSION);
        assertThat(feedItem.getReferenceId()).isEqualTo(session.getId());
        assertThat(feedItem.getTitle()).isEqualTo(session.getTitle());
        assertThat(feedItem.getDescription()).isEqualTo(session.getDescription());
        assertThat(feedItem.getMediaUrl()).isEqualTo(session.getMediaUrl());
        assertThat(feedItem.getThumbnailUrl()).isEqualTo(session.getThumbnailUrl());
        assertThat(feedItem.isPublic()).isEqualTo(session.isPublic());
        assertThat(feedItem.getViewCount()).isEqualTo(0L);
        assertThat(feedItem.getLikeCount()).isEqualTo(0L);
        assertThat(feedItem.getCommentCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should create achievement feed item with correct properties")
    void shouldCreateAchievementFeedItem() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID achievementId = UUID.randomUUID();
        String title = "First Session Achievement";
        String description = "Completed your first reading session";

        // When
        FeedItemEntity feedItem = FeedItemEntityFactory.createAchievementFeedItem(
                userId, achievementId, title, description);

        // Then
        assertThat(feedItem).isNotNull();
        assertThat(feedItem.getUserId()).isEqualTo(userId);
        assertThat(feedItem.getItemType()).isEqualTo(ACHIEVEMENT);
        assertThat(feedItem.getReferenceId()).isEqualTo(achievementId);
        assertThat(feedItem.getTitle()).isEqualTo(title);
        assertThat(feedItem.getDescription()).isEqualTo(description);
        assertThat(feedItem.isPublic()).isTrue();
        assertThat(feedItem.getMetadata()).contains("achievement");
    }

    @Test
    @DisplayName("Should create milestone feed item with correct properties")
    void shouldCreateMilestoneFeedItem() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID milestoneId = UUID.randomUUID();
        String title = "10 Sessions Milestone";
        String description = "Reached 10 completed reading sessions";

        // When
        FeedItemEntity feedItem = FeedItemEntityFactory.createMilestoneFeedItem(
                userId, milestoneId, title, description);

        // Then
        assertThat(feedItem).isNotNull();
        assertThat(feedItem.getUserId()).isEqualTo(userId);
        assertThat(feedItem.getItemType()).isEqualTo(MILESTONE);
        assertThat(feedItem.getReferenceId()).isEqualTo(milestoneId);
        assertThat(feedItem.getTitle()).isEqualTo(title);
        assertThat(feedItem.getDescription()).isEqualTo(description);
        assertThat(feedItem.isPublic()).isTrue();
        assertThat(feedItem.getMetadata()).contains("milestone");
    }

    @Test
    @DisplayName("Should handle session with null optional fields")
    void shouldHandleSessionWithNullOptionalFields() {
        // Given
        SessionEntity session = SessionEntity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Test Session")
                .isPublic(true)
                .durationSeconds(null)
                .fileSizeBytes(null)
                .build();

        // When
        FeedItemEntity feedItem = FeedItemEntityFactory.createFromSession(session);

        // Then
        assertThat(feedItem).isNotNull();
        assertThat(feedItem.getMetadata()).contains("duration\":0");
        assertThat(feedItem.getMetadata()).contains("fileSize\":0");
    }

    @Test
    @DisplayName("Should preserve privacy setting from session")
    void shouldPreservePrivacySettingFromSession() {
        // Given
        SessionEntity baseSession = createTestSessionEntity();
        SessionEntity privateSession = SessionEntity.builder()
                .id(baseSession.getId())
                .userId(baseSession.getUserId())
                .title(baseSession.getTitle())
                .description(baseSession.getDescription())
                .mediaUrl(baseSession.getMediaUrl())
                .thumbnailUrl(baseSession.getThumbnailUrl())
                .mediaType(baseSession.getMediaType())
                .durationSeconds(baseSession.getDurationSeconds())
                .fileSizeBytes(baseSession.getFileSizeBytes())
                .isPublic(false)
                .processingStatus(baseSession.getProcessingStatus())
                .createdAt(baseSession.getCreatedAt())
                .updatedAt(baseSession.getUpdatedAt())
                .build();

        // When
        FeedItemEntity feedItem = FeedItemEntityFactory.createFromSession(privateSession);

        // Then
        assertThat(feedItem.isPublic()).isFalse();
    }

    private SessionEntity createTestSessionEntity() {
        return SessionEntity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Test Reading Session")
                .description("A test session for reading practice")
                .mediaUrl("https://example.com/session.mp4")
                .thumbnailUrl("https://example.com/thumb.jpg")
                .mediaType(MediaType.AUDIO)
                .durationSeconds(300)
                .fileSizeBytes(1024000L)
                .isPublic(true)
                .processingStatus(ProcessingStatus.COMPLETED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}