package org.readtogether.library.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.library.entity.BookEntity;
import org.readtogether.library.entity.BookProgressEntity;
import org.readtogether.library.entity.BookSessionEntity;
import org.readtogether.library.common.enums.BookCategory;
import org.readtogether.library.common.enums.BookStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class LibraryEntityFixtures {

    public static final UUID DEFAULT_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final UUID DEFAULT_BOOK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    public static final UUID DEFAULT_PROGRESS_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    public static final UUID DEFAULT_SESSION_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");

    // BookEntity fixtures
    public static BookEntity createDefaultBookEntity() {
        return BookEntity.builder()
                .id(DEFAULT_BOOK_ID)
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("9780743273565")
                .category(BookCategory.FICTION)
                .description("A classic American novel set in the summer of 1922")
                .coverImageUrl("https://example.com/covers/gatsby.jpg")
                .totalPages(180)
                .estimatedReadingMinutes(360)
                .language("en")
                .publisher("Scribner")
                .publicationYear(1925)
                .addedByUserId(DEFAULT_USER_ID)
                .isPublic(false)
                .difficultyLevel(3)
                .externalId("ext123")
                .externalSource("goodreads")
                .build();
    }

    public static BookEntity createBookEntity(
            UUID id,
            UUID ownerId,
            String isbn,
            String title,
            String author,
            BookCategory category,
            boolean isPublic) {
        
        return BookEntity.builder()
                .id(id)
                .title(title)
                .author(author)
                .isbn(isbn)
                .category(category)
                .description("Description for " + title)
                .coverImageUrl("https://example.com/covers/" + id + ".jpg")
                .totalPages(200)
                .estimatedReadingMinutes(400)
                .language("en")
                .publisher("Test Publisher")
                .publicationYear(2023)
                .addedByUserId(ownerId)
                .isPublic(isPublic)
                .difficultyLevel(2)
                .build();
    }

    public static BookEntity createBookEntityForPersistence(
            UUID ownerId,
            String isbn,
            String title,
            String author,
            BookCategory category,
            boolean isPublic) {
        
        return BookEntity.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .category(category)
                .description("Description for " + title)
                .coverImageUrl("https://example.com/covers/test.jpg")
                .totalPages(200)
                .estimatedReadingMinutes(400)
                .language("en")
                .publisher("Test Publisher")
                .publicationYear(2023)
                .addedByUserId(ownerId)
                .isPublic(isPublic)
                .difficultyLevel(2)
                .build();
    }

    public static BookEntity createPublicBookEntity() {
        return createBookEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                "9781234567890",
                "Public Book",
                "Public Author",
                BookCategory.EDUCATIONAL,
                true
        );
    }

    // BookProgressEntity fixtures
    public static BookProgressEntity createDefaultBookProgressEntity() {
        return BookProgressEntity.builder()
                .id(DEFAULT_PROGRESS_ID)
                .userId(DEFAULT_USER_ID)
                .bookId(DEFAULT_BOOK_ID)
                .status(BookStatus.IN_PROGRESS)
                .currentPage(50)
                .progressPercentage(25)
                .totalSessionsCompleted(5)
                .totalReadingTimeSeconds(3600L)
                .startedReadingAt(LocalDateTime.now().minusDays(10))
                .lastReadAt(LocalDateTime.now().minusDays(1))
                .notes("Great book so far!")
                .isFavorite(true)
                .personalRating(4)
                .readingGoalPagesPerDay(10)
                .build();
    }

    public static BookProgressEntity createBookProgressEntity(
            UUID id,
            UUID userId,
            UUID bookId,
            BookStatus status,
            int currentPage,
            int progressPercentage,
            boolean isFavorite) {
        
        return BookProgressEntity.builder()
                .id(id)
                .userId(userId)
                .bookId(bookId)
                .status(status)
                .currentPage(currentPage)
                .progressPercentage(progressPercentage)
                .totalSessionsCompleted(3)
                .totalReadingTimeSeconds(1800L)
                .startedReadingAt(LocalDateTime.now().minusDays(5))
                .lastReadAt(LocalDateTime.now().minusDays(1))
                .isFavorite(isFavorite)
                .personalRating(status == BookStatus.COMPLETED ? 5 : null)
                .readingGoalPagesPerDay(15)
                .build();
    }

    public static BookProgressEntity createBookProgressEntityForPersistence(
            UUID userId,
            UUID bookId,
            BookStatus status,
            int currentPage,
            int progressPercentage,
            boolean isFavorite) {
        
        return BookProgressEntity.builder()
                .userId(userId)
                .bookId(bookId)
                .status(status)
                .currentPage(currentPage)
                .progressPercentage(progressPercentage)
                .totalSessionsCompleted(3)
                .totalReadingTimeSeconds(1800L)
                .startedReadingAt(LocalDateTime.now().minusDays(5))
                .lastReadAt(LocalDateTime.now().minusDays(1))
                .isFavorite(isFavorite)
                .personalRating(status == BookStatus.COMPLETED ? 5 : null)
                .readingGoalPagesPerDay(15)
                .build();
    }

    public static BookProgressEntity createCompletedBookProgressEntity() {
        return createBookProgressEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                DEFAULT_BOOK_ID,
                BookStatus.COMPLETED,
                180,
                100,
                true
        );
    }

    // BookSessionEntity fixtures
    public static BookSessionEntity createDefaultBookSessionEntity() {
        return BookSessionEntity.builder()
                .id(DEFAULT_SESSION_ID)
                .userId(DEFAULT_USER_ID)
                .bookId(DEFAULT_BOOK_ID)
                .sessionId(UUID.randomUUID())
                .readingTimeSeconds(1800)
                .pagesRead(10)
                .startPage(40)
                .endPage(50)
                .sessionNotes("Good reading session today")
                .comprehensionRating(4)
                .difficultyRating(3)
                .build();
    }

    public static BookSessionEntity createBookSessionEntity(
            UUID id,
            UUID userId,
            UUID bookId,
            UUID sessionId,
            int readingTimeSeconds,
            int pagesRead,
            int startPage,
            int endPage) {
        
        return BookSessionEntity.builder()
                .id(id)
                .userId(userId)
                .bookId(bookId)
                .sessionId(sessionId)
                .readingTimeSeconds(readingTimeSeconds)
                .pagesRead(pagesRead)
                .startPage(startPage)
                .endPage(endPage)
                .sessionNotes("Session notes")
                .comprehensionRating(4)
                .difficultyRating(2)
                .build();
    }

    public static BookSessionEntity createBookSessionEntityForPersistence(
            UUID userId,
            UUID bookId,
            UUID sessionId,
            int readingTimeSeconds,
            int pagesRead,
            int startPage,
            int endPage) {
        
        return BookSessionEntity.builder()
                .userId(userId)
                .bookId(bookId)
                .sessionId(sessionId)
                .readingTimeSeconds(readingTimeSeconds)
                .pagesRead(pagesRead)
                .startPage(startPage)
                .endPage(endPage)
                .sessionNotes("Session notes")
                .comprehensionRating(4)
                .difficultyRating(2)
                .build();
    }

    public static BookSessionEntity createRecentBookSessionEntity() {
        return createBookSessionEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                DEFAULT_BOOK_ID,
                UUID.randomUUID(),
                1200,
                8,
                20,
                28
        );
    }
}