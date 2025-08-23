package org.readtogether.library.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.library.model.response.BookResponse;
import org.readtogether.library.model.response.BookProgressResponse;
import org.readtogether.library.common.enums.BookCategory;
import org.readtogether.library.common.enums.BookStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@UtilityClass
public class LibraryResponseFixtures {

    // BookResponse fixtures
    public static BookResponse createDefaultBookResponse() {

        return BookResponse.builder()
                .id(LibraryEntityFixtures.DEFAULT_BOOK_ID)
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
                .addedByUserId(LibraryEntityFixtures.DEFAULT_USER_ID)
                .isPublic(false)
                .difficultyLevel(3)
                .externalId("ext123")
                .externalSource("goodreads")
                .createdAt(Instant.now().minus(10, ChronoUnit.DAYS))
                .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
    }

    public static BookResponse createBookResponse(
            UUID id,
            String title,
            String author,
            BookCategory category,
            boolean isPublic) {

        return BookResponse.builder()
                .id(id)
                .title(title)
                .author(author)
                .isbn("9781234567890")
                .category(category)
                .description("Description for " + title)
                .coverImageUrl("https://example.com/covers/" + id + ".jpg")
                .totalPages(200)
                .estimatedReadingMinutes(400)
                .language("en")
                .publisher("Test Publisher")
                .publicationYear(2023)
                .addedByUserId(LibraryEntityFixtures.DEFAULT_USER_ID)
                .isPublic(isPublic)
                .difficultyLevel(2)
                .createdAt(Instant.now().minus(5, ChronoUnit.DAYS))
                .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
    }

    // BookProgressResponse fixtures
    public static BookProgressResponse createDefaultBookProgressResponse() {

        return BookProgressResponse.builder()
                .id(LibraryEntityFixtures.DEFAULT_PROGRESS_ID)
                .userId(LibraryEntityFixtures.DEFAULT_USER_ID)
                .bookId(LibraryEntityFixtures.DEFAULT_BOOK_ID)
                .status(BookStatus.IN_PROGRESS)
                .currentPage(50)
                .progressPercentage(25)
                .totalSessionsCompleted(5)
                .totalReadingTimeSeconds(3600L)
                .startedReadingAt(Instant.now().minus(10, ChronoUnit.DAYS))
                .lastReadAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .notes("Great book so far!")
                .favoriteQuotes("\"So we beat on, boats against the current\"")
                .personalRating(4)
                .readingGoalPagesPerDay(10)
                .isFavorite(true)
                .createdAt(Instant.now().minus(10, ChronoUnit.DAYS))
                .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
    }

    public static BookProgressResponse createBookProgressResponse(
            UUID id,
            UUID userId,
            UUID bookId,
            BookStatus status,
            int progressPercentage,
            boolean isFavorite) {

        return BookProgressResponse.builder()
                .id(id)
                .userId(userId)
                .bookId(bookId)
                .status(status)
                .currentPage(progressPercentage * 2) // Estimation
                .progressPercentage(progressPercentage)
                .totalSessionsCompleted(3)
                .totalReadingTimeSeconds(1800L)
                .startedReadingAt(Instant.now().minus(5, ChronoUnit.DAYS))
                .lastReadAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .isFavorite(isFavorite)
                .personalRating(status == BookStatus.COMPLETED ? 5 : null)
                .readingGoalPagesPerDay(15)
                .createdAt(Instant.now().minus(5, ChronoUnit.DAYS))
                .updatedAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
    }
}