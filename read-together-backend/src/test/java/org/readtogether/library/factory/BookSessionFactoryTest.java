package org.readtogether.library.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.library.entity.BookSessionEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookSessionFactory Tests")
class BookSessionFactoryTest {

    private static final UUID TEST_SESSION_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174011");
    private static final UUID TEST_BOOK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

    @Test
    @DisplayName("Should create book session entity from parameters")
    void shouldCreateBookSessionEntityFromParameters() {
        // Given
        Integer pagesRead = 15;
        Integer readingTimeSeconds = 1800;

        // When
        BookSessionEntity result = BookSessionFactory.createBookSessionEntity(
                TEST_SESSION_ID,
                TEST_BOOK_ID,
                TEST_USER_ID,
                pagesRead,
                readingTimeSeconds
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // Should be null for new entities
        assertThat(result.getSessionId()).isEqualTo(TEST_SESSION_ID);
        assertThat(result.getBookId()).isEqualTo(TEST_BOOK_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getPagesRead()).isEqualTo(pagesRead);
        assertThat(result.getReadingTimeSeconds()).isEqualTo(readingTimeSeconds);
        
        // Other fields should be null/default
        assertThat(result.getStartPage()).isNull();
        assertThat(result.getEndPage()).isNull();
        assertThat(result.getSessionNotes()).isNull();
        assertThat(result.getDifficultyRating()).isNull();
        assertThat(result.getComprehensionRating()).isNull();
    }

    @Test
    @DisplayName("Should handle null pages read and reading time")
    void shouldHandleNullPagesReadAndReadingTime() {
        // When
        BookSessionEntity result = BookSessionFactory.createBookSessionEntity(
                TEST_SESSION_ID,
                TEST_BOOK_ID,
                TEST_USER_ID,
                null,
                null
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSessionId()).isEqualTo(TEST_SESSION_ID);
        assertThat(result.getBookId()).isEqualTo(TEST_BOOK_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getPagesRead()).isNull();
        assertThat(result.getReadingTimeSeconds()).isNull();
    }

    @Test
    @DisplayName("Should handle zero values for pages and time")
    void shouldHandleZeroValuesForPagesAndTime() {
        // When
        BookSessionEntity result = BookSessionFactory.createBookSessionEntity(
                TEST_SESSION_ID,
                TEST_BOOK_ID,
                TEST_USER_ID,
                0,
                0
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSessionId()).isEqualTo(TEST_SESSION_ID);
        assertThat(result.getBookId()).isEqualTo(TEST_BOOK_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getPagesRead()).isEqualTo(0);
        assertThat(result.getReadingTimeSeconds()).isEqualTo(0);
    }
}