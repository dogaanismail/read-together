package org.readtogether.library.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.request.BookUpdateRequest;
import org.readtogether.library.model.request.BookProgressUpdateRequest;
import org.readtogether.library.common.enums.BookCategory;
import org.readtogether.library.common.enums.BookStatus;

import java.util.UUID;

@UtilityClass
public class LibraryRequestFixtures {

    // BookCreateRequest fixtures
    public static BookCreateRequest createDefaultAddBookRequest() {
        return BookCreateRequest.builder()
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
                .isPublic(false)
                .difficultyLevel(3)
                .externalId("ext123")
                .externalSource("goodreads")
                .build();
    }

    public static BookCreateRequest createAddBookRequest(
            String isbn,
            String title,
            String author,
            BookCategory category,
            String source,
            String externalId,
            String coverUrl) {
        
        return BookCreateRequest.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .category(category)
                .description("Description for " + title)
                .coverImageUrl(coverUrl)
                .totalPages(200)
                .estimatedReadingMinutes(400)
                .language("en")
                .publisher("Test Publisher")
                .publicationYear(2023)
                .isPublic(false)
                .difficultyLevel(2)
                .externalId(externalId)
                .externalSource(source)
                .build();
    }

    public static BookCreateRequest createPublicBookRequest() {
        return BookCreateRequest.builder()
                .title("Public Educational Book")
                .author("Education Expert")
                .isbn("9781234567890")
                .category(BookCategory.EDUCATIONAL)
                .description("A book for everyone to learn from")
                .coverImageUrl("https://example.com/covers/public.jpg")
                .totalPages(300)
                .estimatedReadingMinutes(600)
                .language("en")
                .publisher("Public Publisher")
                .publicationYear(2024)
                .isPublic(true)
                .difficultyLevel(2)
                .build();
    }

    public static BookCreateRequest createMinimalBookRequest() {
        return BookCreateRequest.builder()
                .title("Minimal Book")
                .author("Author Name")
                .category(BookCategory.OTHER)
                .build();
    }

    // BookUpdateRequest fixtures
    public static BookUpdateRequest createDefaultUpdateRequest() {
        return BookUpdateRequest.builder()
                .title("Updated Title")
                .author("Updated Author")
                .description("Updated description")
                .coverImageUrl("https://example.com/covers/updated.jpg")
                .totalPages(250)
                .estimatedReadingMinutes(500)
                .publisher("Updated Publisher")
                .publicationYear(2024)
                .isPublic(true)
                .difficultyLevel(4)
                .build();
    }

    public static BookUpdateRequest createPartialUpdateRequest() {
        return BookUpdateRequest.builder()
                .title("Partially Updated Title")
                .description("Only updating title and description")
                .build();
    }

    // BookProgressUpdateRequest fixtures
    public static BookProgressUpdateRequest createDefaultUpdateProgressRequest() {
        return BookProgressUpdateRequest.builder()
                .status(BookStatus.IN_PROGRESS)
                .currentPage(50)
                .progressPercentage(25)
                .notes("Making good progress")
                .favoriteQuotes("\"So we beat on, boats against the current\"")
                .personalRating(4)
                .readingGoalPagesPerDay(10)
                .isFavorite(true)
                .build();
    }

    public static BookProgressUpdateRequest createUpdateProgressRequest(
            UUID bookId,
            Integer progressPercent,
            Integer currentPage) {
        
        return BookProgressUpdateRequest.builder()
                .status(progressPercent >= 100 ? BookStatus.COMPLETED : BookStatus.IN_PROGRESS)
                .currentPage(currentPage)
                .progressPercentage(progressPercent)
                .build();
    }

    public static BookProgressUpdateRequest createToggleFavoriteRequest(
            UUID bookId,
            boolean favorite) {
        
        return BookProgressUpdateRequest.builder()
                .isFavorite(favorite)
                .build();
    }

    public static BookProgressUpdateRequest createCompleteBookRequest() {
        return BookProgressUpdateRequest.builder()
                .status(BookStatus.COMPLETED)
                .currentPage(180)
                .progressPercentage(100)
                .notes("Finished the book! Great read.")
                .personalRating(5)
                .isFavorite(true)
                .build();
    }

    public static BookProgressUpdateRequest createStartReadingRequest() {
        return BookProgressUpdateRequest.builder()
                .status(BookStatus.IN_PROGRESS)
                .currentPage(1)
                .progressPercentage(1)
                .notes("Starting this book today")
                .readingGoalPagesPerDay(20)
                .build();
    }

    public static BookProgressUpdateRequest createOnHoldRequest() {
        return BookProgressUpdateRequest.builder()
                .status(BookStatus.ON_HOLD)
                .notes("Putting this book on hold for now")
                .build();
    }

    public static BookProgressUpdateRequest createInvalidProgressRequest() {
        return BookProgressUpdateRequest.builder()
                .status(BookStatus.IN_PROGRESS)
                .currentPage(-1) // Invalid page
                .progressPercentage(150) // Invalid percentage > 100
                .personalRating(6) // Invalid rating > 5
                .build();
    }

    public static BookProgressUpdateRequest createProgressBoundsRequest(
            int currentPage,
            int progressPercentage) {
        
        return BookProgressUpdateRequest.builder()
                .status(BookStatus.IN_PROGRESS)
                .currentPage(currentPage)
                .progressPercentage(progressPercentage)
                .build();
    }
}