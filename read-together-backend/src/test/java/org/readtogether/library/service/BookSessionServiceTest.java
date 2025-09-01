package org.readtogether.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.readtogether.library.entity.BookSessionEntity;
import org.readtogether.library.repository.BookSessionRepository;
import org.readtogether.library.fixtures.LibraryEntityFixtures;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BookSessionService Tests")
class BookSessionServiceTest {

    private static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174013");
    private static final UUID TEST_BOOK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID TEST_SESSION_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

    @Mock
    private BookSessionRepository bookSessionRepository;

    @InjectMocks
    private BookSessionService bookSessionService;

    private BookSessionEntity testSessionEntity;

    @BeforeEach
    void setUp() {
        testSessionEntity = LibraryEntityFixtures.createDefaultBookSessionEntity();
    }

    @Test
    @DisplayName("Should get user sessions")
    void shouldGetUserSessions() {
        // Given
        List<BookSessionEntity> userSessions = List.of(testSessionEntity);
        when(bookSessionRepository.findByUserIdOrderByCreatedAtDesc(TEST_USER_ID))
                .thenReturn(userSessions);

        // When
        List<BookSessionEntity> result = bookSessionService.getUserSessions(TEST_USER_ID);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testSessionEntity);
        verify(bookSessionRepository).findByUserIdOrderByCreatedAtDesc(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should get user book sessions")
    void shouldGetUserBookSessions() {
        // Given
        List<BookSessionEntity> bookSessions = List.of(testSessionEntity);
        when(bookSessionRepository.findByUserIdAndBookIdOrderByCreatedAtDesc(TEST_USER_ID, TEST_BOOK_ID))
                .thenReturn(bookSessions);

        // When
        List<BookSessionEntity> result = bookSessionService.getUserBookSessions(TEST_USER_ID, TEST_BOOK_ID);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testSessionEntity);
        verify(bookSessionRepository).findByUserIdAndBookIdOrderByCreatedAtDesc(TEST_USER_ID, TEST_BOOK_ID);
    }

    @Test
    @DisplayName("Should get recent user sessions")
    void shouldGetRecentUserSessions() {
        // Given
        int days = 30;
        List<BookSessionEntity> recentSessions = List.of(testSessionEntity);

        when(bookSessionRepository.findRecentSessionsByUserId(eq(TEST_USER_ID), any(Instant.class)))
                .thenReturn(recentSessions);

        // When
        List<BookSessionEntity> result = bookSessionService.getRecentUserSessions(TEST_USER_ID, days);

        // Then
        assertThat(result).hasSize(1);
        verify(bookSessionRepository).findRecentSessionsByUserId(eq(TEST_USER_ID), any(Instant.class));
    }

    @Test
    @DisplayName("Should get total reading time for book")
    void shouldGetTotalReadingTimeForBook() {
        // Given
        Long expectedReadingTime = 3600L;
        when(bookSessionRepository.getTotalReadingTimeForUserBook(TEST_USER_ID, TEST_BOOK_ID))
                .thenReturn(expectedReadingTime);

        // When
        Long result = bookSessionService.getTotalReadingTimeForUserBook(TEST_USER_ID, TEST_BOOK_ID);

        // Then
        assertThat(result).isEqualTo(expectedReadingTime);
        verify(bookSessionRepository).getTotalReadingTimeForUserBook(TEST_USER_ID, TEST_BOOK_ID);
    }

    @Test
    @DisplayName("Should get total pages read for book")
    void shouldGetTotalPagesReadForBook() {
        // Given
        Integer expectedPagesRead = 150;
        when(bookSessionRepository.getTotalPagesReadForUserBook(TEST_USER_ID, TEST_BOOK_ID))
                .thenReturn(expectedPagesRead);

        // When
        Integer result = bookSessionService.getTotalPagesReadForUserBook(TEST_USER_ID, TEST_BOOK_ID);

        // Then
        assertThat(result).isEqualTo(expectedPagesRead);
        verify(bookSessionRepository).getTotalPagesReadForUserBook(TEST_USER_ID, TEST_BOOK_ID);
    }

    @Test
    @DisplayName("Should get session count for book")
    void shouldGetSessionCountForBook() {
        // Given
        long expectedSessionCount = 5L;
        when(bookSessionRepository.countSessionsForUserBook(TEST_USER_ID, TEST_BOOK_ID))
                .thenReturn(expectedSessionCount);

        // When
        long result = bookSessionService.getSessionCountForUserBook(TEST_USER_ID, TEST_BOOK_ID);

        // Then
        assertThat(result).isEqualTo(expectedSessionCount);
        verify(bookSessionRepository).countSessionsForUserBook(TEST_USER_ID, TEST_BOOK_ID);
    }

    @Test
    @DisplayName("Should delete book session")
    void shouldDeleteBookSession() {
        // When
        bookSessionService.deleteBookSession(TEST_SESSION_ID);

        // Then
        verify(bookSessionRepository).deleteBySessionId(TEST_SESSION_ID);
    }

    @Test
    @DisplayName("Should check if session exists")
    void shouldCheckIfSessionExists() {
        // Given
        when(bookSessionRepository.existsBySessionId(TEST_SESSION_ID))
                .thenReturn(true);

        // When
        Boolean result = bookSessionService.existsBySessionId(TEST_SESSION_ID);

        // Then
        assertThat(result).isTrue();
        verify(bookSessionRepository).existsBySessionId(TEST_SESSION_ID);
    }

    @Test
    @DisplayName("Should return false when session does not exist")
    void shouldReturnFalseWhenSessionDoesNotExist() {
        // Given
        when(bookSessionRepository.existsBySessionId(TEST_SESSION_ID))
                .thenReturn(false);

        // When
        Boolean result = bookSessionService.existsBySessionId(TEST_SESSION_ID);

        // Then
        assertThat(result).isFalse();
        verify(bookSessionRepository).existsBySessionId(TEST_SESSION_ID);
    }

    @Test
    @DisplayName("Should handle empty user sessions list")
    void shouldHandleEmptyUserSessionsList() {
        // Given
        when(bookSessionRepository.findByUserIdOrderByCreatedAtDesc(TEST_USER_ID))
                .thenReturn(List.of());

        // When
        List<BookSessionEntity> result = bookSessionService.getUserSessions(TEST_USER_ID);

        // Then
        assertThat(result).isEmpty();
        verify(bookSessionRepository).findByUserIdOrderByCreatedAtDesc(TEST_USER_ID);
    }
}