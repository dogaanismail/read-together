package org.readtogether.library.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.library.entity.BookProgressEntity;
import org.readtogether.library.model.request.BookProgressUpdateRequest;
import org.readtogether.library.fixtures.LibraryRequestFixtures;
import org.readtogether.library.fixtures.LibraryEntityFixtures;
import org.readtogether.library.common.enums.BookStatus;
import org.readtogether.library.model.response.BookProgressResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookProgressFactory Tests")
class BookProgressFactoryTest {

    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174016");
    private static final UUID TEST_BOOK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

    @Test
    @DisplayName("Should create progress entity with default values")
    void shouldCreateProgressEntityWithDefaultValues() {
        // When
        BookProgressEntity result = BookProgressFactory.createProgressEntity(TEST_USER_ID, TEST_BOOK_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // Should be null for new entities
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getBookId()).isEqualTo(TEST_BOOK_ID);
        assertThat(result.getStatus()).isEqualTo(BookStatus.NOT_STARTED);
        assertThat(result.getCurrentPage()).isEqualTo(0);
        assertThat(result.getProgressPercentage()).isEqualTo(0);
        assertThat(result.getTotalSessionsCompleted()).isEqualTo(0);
        assertThat(result.getTotalReadingTimeSeconds()).isEqualTo(0L);
        assertThat(result.isFavorite()).isFalse();
        assertThat(result.getNotes()).isNull();
        assertThat(result.getFavoriteQuotes()).isNull();
        assertThat(result.getPersonalRating()).isNull();
        assertThat(result.getReadingGoalPagesPerDay()).isNull();
        assertThat(result.getStartedReadingAt()).isNull();
        assertThat(result.getLastReadAt()).isNull();
        assertThat(result.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should update progress entity from update request")
    void shouldUpdateProgressEntityFromUpdateRequest() {
        // Given
        BookProgressEntity existingEntity = LibraryEntityFixtures.createDefaultBookProgressEntity();
        BookProgressUpdateRequest updateRequest = LibraryRequestFixtures.createDefaultUpdateProgressRequest();

        // When
        BookProgressFactory.updateProgressEntity(existingEntity, updateRequest);

        // Then
        assertThat(existingEntity.getStatus()).isEqualTo(updateRequest.getStatus());
        assertThat(existingEntity.getCurrentPage()).isEqualTo(updateRequest.getCurrentPage());
        assertThat(existingEntity.getProgressPercentage()).isEqualTo(updateRequest.getProgressPercentage());
        assertThat(existingEntity.getNotes()).isEqualTo(updateRequest.getNotes());
        assertThat(existingEntity.getFavoriteQuotes()).isEqualTo(updateRequest.getFavoriteQuotes());
        assertThat(existingEntity.getPersonalRating()).isEqualTo(updateRequest.getPersonalRating());
        assertThat(existingEntity.getReadingGoalPagesPerDay()).isEqualTo(updateRequest.getReadingGoalPagesPerDay());
        assertThat(existingEntity.isFavorite()).isEqualTo(updateRequest.getIsFavorite());

        // Fields that should not change
        assertThat(existingEntity.getId()).isEqualTo(LibraryEntityFixtures.DEFAULT_PROGRESS_ID);
        assertThat(existingEntity.getUserId()).isEqualTo(LibraryEntityFixtures.DEFAULT_USER_ID);
        assertThat(existingEntity.getBookId()).isEqualTo(LibraryEntityFixtures.DEFAULT_BOOK_ID);
    }

    @Test
    @DisplayName("Should handle partial updates without affecting unchanged fields")
    void shouldHandlePartialUpdatesWithoutAffectingUnchangedFields() {
        // Given
        BookProgressEntity existingEntity = LibraryEntityFixtures.createDefaultBookProgressEntity();
        BookStatus originalStatus = existingEntity.getStatus();
        int originalCurrentPage = existingEntity.getCurrentPage();

        BookProgressUpdateRequest partialUpdate = BookProgressUpdateRequest.builder()
                .progressPercentage(75)
                .notes("Updated notes only")
                .build();

        // When
        BookProgressFactory.updateProgressEntity(existingEntity, partialUpdate);

        // Then
        assertThat(existingEntity.getProgressPercentage()).isEqualTo(75);
        assertThat(existingEntity.getNotes()).isEqualTo("Updated notes only");

        // Unchanged fields should remain the same
        assertThat(existingEntity.getStatus()).isEqualTo(originalStatus);
        assertThat(existingEntity.getCurrentPage()).isEqualTo(originalCurrentPage);
        assertThat(existingEntity.getPersonalRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should handle null values in update request")
    void shouldHandleNullValuesInUpdateRequest() {
        // Given
        BookProgressEntity existingEntity = LibraryEntityFixtures.createDefaultBookProgressEntity();
        String originalNotes = existingEntity.getNotes();

        BookProgressUpdateRequest updateWithNulls = BookProgressUpdateRequest.builder()
                .status(null)
                .currentPage(null)
                .progressPercentage(null)
                .notes(null)
                .favoriteQuotes(null)
                .personalRating(null)
                .readingGoalPagesPerDay(null)
                .isFavorite(null)
                .build();

        // When
        BookProgressFactory.updateProgressEntity(existingEntity, updateWithNulls);

        // Then - nothing should change when all fields are null
        assertThat(existingEntity.getStatus()).isEqualTo(BookStatus.IN_PROGRESS);
        assertThat(existingEntity.getCurrentPage()).isEqualTo(50);
        assertThat(existingEntity.getProgressPercentage()).isEqualTo(25);
        assertThat(existingEntity.getNotes()).isEqualTo(originalNotes);
        assertThat(existingEntity.isFavorite()).isTrue();
    }

    @Test
    @DisplayName("Should handle favorite toggle updates")
    void shouldHandleFavoriteToggleUpdates() {
        // Given
        BookProgressEntity entity = LibraryEntityFixtures.createDefaultBookProgressEntity();
        assertThat(entity.isFavorite()).isTrue(); // From fixture

        BookProgressUpdateRequest toggleOff = LibraryRequestFixtures.createToggleFavoriteRequest(TEST_BOOK_ID, false);

        // When
        BookProgressFactory.updateProgressEntity(entity, toggleOff);

        // Then
        assertThat(entity.isFavorite()).isFalse();

        // When - toggle back on
        BookProgressUpdateRequest toggleOn = LibraryRequestFixtures.createToggleFavoriteRequest(TEST_BOOK_ID, true);
        BookProgressFactory.updateProgressEntity(entity, toggleOn);

        // Then
        assertThat(entity.isFavorite()).isTrue();
    }

    @Test
    @DisplayName("Should handle completion updates")
    void shouldHandleCompletionUpdates() {
        // Given
        BookProgressEntity entity = BookProgressFactory.createProgressEntity(TEST_USER_ID, TEST_BOOK_ID);
        BookProgressUpdateRequest completeRequest = LibraryRequestFixtures.createCompleteBookRequest();

        // When
        BookProgressFactory.updateProgressEntity(entity, completeRequest);

        // Then
        assertThat(entity.getStatus()).isEqualTo(BookStatus.COMPLETED);
        assertThat(entity.getCurrentPage()).isEqualTo(180);
        assertThat(entity.getProgressPercentage()).isEqualTo(100);
        assertThat(entity.getPersonalRating()).isEqualTo(5);
        assertThat(entity.isFavorite()).isTrue();
        assertThat(entity.getNotes()).isEqualTo("Finished the book! Great read.");
    }

    @Test
    @DisplayName("Should create progress response from entity")
    void shouldCreateProgressResponseFromEntity() {
        // Given
        BookProgressEntity entity = LibraryEntityFixtures.createDefaultBookProgressEntity();
        entity.setCreatedAt(java.time.Instant.now().minusSeconds(86400));
        entity.setUpdatedAt(java.time.Instant.now());

        // When
        BookProgressResponse result = BookProgressFactory.createProgressResponse(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getUserId()).isEqualTo(entity.getUserId());
        assertThat(result.getBookId()).isEqualTo(entity.getBookId());
        assertThat(result.getStatus()).isEqualTo(entity.getStatus());
        assertThat(result.getCurrentPage()).isEqualTo(entity.getCurrentPage());
        assertThat(result.getProgressPercentage()).isEqualTo(entity.getProgressPercentage());
        assertThat(result.getTotalSessionsCompleted()).isEqualTo(entity.getTotalSessionsCompleted());
        assertThat(result.getTotalReadingTimeSeconds()).isEqualTo(entity.getTotalReadingTimeSeconds());
        assertThat(result.getStartedReadingAt()).isEqualTo(entity.getStartedReadingAt());
        assertThat(result.getLastReadAt()).isEqualTo(entity.getLastReadAt());
        assertThat(result.getCompletedAt()).isEqualTo(entity.getCompletedAt());
        assertThat(result.getNotes()).isEqualTo(entity.getNotes());
        assertThat(result.getFavoriteQuotes()).isEqualTo(entity.getFavoriteQuotes());
        assertThat(result.getPersonalRating()).isEqualTo(entity.getPersonalRating());
        assertThat(result.getReadingGoalPagesPerDay()).isEqualTo(entity.getReadingGoalPagesPerDay());
        assertThat(result.isFavorite()).isEqualTo(entity.isFavorite());
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

}