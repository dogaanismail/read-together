package org.readtogether.library.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.library.entity.BookEntity;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.request.BookUpdateRequest;
import org.readtogether.library.model.response.BookResponse;
import org.readtogether.library.fixtures.LibraryRequestFixtures;
import org.readtogether.library.fixtures.LibraryEntityFixtures;
import org.readtogether.library.common.enums.BookCategory;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookFactory Tests")
class BookFactoryTest {

    public static final UUID TEST_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174015");

    @Test
    @DisplayName("Should create book entity from add request")
    void shouldCreateBookEntityFromAddRequest() {
        // Given
        BookCreateRequest request = LibraryRequestFixtures.createDefaultAddBookRequest();

        // When
        BookEntity result = BookFactory.createBookEntity(request, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // Should be null for new entities
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getAuthor()).isEqualTo(request.getAuthor());
        assertThat(result.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(result.getCategory()).isEqualTo(request.getCategory());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
        assertThat(result.getCoverImageUrl()).isEqualTo(request.getCoverImageUrl());
        assertThat(result.getTotalPages()).isEqualTo(request.getTotalPages());
        assertThat(result.getEstimatedReadingMinutes()).isEqualTo(request.getEstimatedReadingMinutes());
        assertThat(result.getLanguage()).isEqualTo(request.getLanguage());
        assertThat(result.getPublisher()).isEqualTo(request.getPublisher());
        assertThat(result.getPublicationYear()).isEqualTo(request.getPublicationYear());
        assertThat(result.getAddedByUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.isPublic()).isEqualTo(request.isPublic());
        assertThat(result.getDifficultyLevel()).isEqualTo(request.getDifficultyLevel());
        assertThat(result.getExternalId()).isEqualTo(request.getExternalId());
        assertThat(result.getExternalSource()).isEqualTo(request.getExternalSource());
    }

    @Test
    @DisplayName("Should default unset fields when creating book entity")
    void shouldDefaultUnsetFields() {
        // Given
        BookCreateRequest request = LibraryRequestFixtures.createMinimalBookRequest();

        // When
        BookEntity result = BookFactory.createBookEntity(request, TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Minimal Book");
        assertThat(result.getAuthor()).isEqualTo("Author Name");
        assertThat(result.getCategory()).isEqualTo(BookCategory.OTHER);
        assertThat(result.getAddedByUserId()).isEqualTo(TEST_USER_ID);

        // Default values
        assertThat(result.getLanguage()).isEqualTo("en"); // Default language
        assertThat(result.isPublic()).isFalse(); // Default visibility
        assertThat(result.getIsbn()).isNull();
        assertThat(result.getDescription()).isNull();
        assertThat(result.getCoverImageUrl()).isNull();
        assertThat(result.getTotalPages()).isNull();
        assertThat(result.getEstimatedReadingMinutes()).isNull();
        assertThat(result.getPublisher()).isNull();
        assertThat(result.getPublicationYear()).isNull();
        assertThat(result.getDifficultyLevel()).isNull();
        assertThat(result.getExternalId()).isNull();
        assertThat(result.getExternalSource()).isNull();
    }

    @Test
    @DisplayName("Should handle null language by defaulting to English")
    void shouldHandleNullLanguageByDefaultingToEnglish() {
        // Given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("Test Book")
                .author("Test Author")
                .category(BookCategory.FICTION)
                .language(null)
                .build();

        // When
        BookEntity result = BookFactory.createBookEntity(request, TEST_USER_ID);

        // Then
        assertThat(result.getLanguage()).isEqualTo("en");
    }

    @Test
    @DisplayName("Should update book entity from update request")
    void shouldUpdateBookEntityFromUpdateRequest() {
        // Given
        BookEntity existingEntity = LibraryEntityFixtures.createDefaultBookEntity();
        BookUpdateRequest updateRequest = LibraryRequestFixtures.createDefaultUpdateRequest();

        // When
        BookFactory.updateBookEntity(existingEntity, updateRequest);

        // Then
        assertThat(existingEntity.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(existingEntity.getAuthor()).isEqualTo(updateRequest.getAuthor());
        assertThat(existingEntity.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(existingEntity.getCoverImageUrl()).isEqualTo(updateRequest.getCoverImageUrl());
        assertThat(existingEntity.getTotalPages()).isEqualTo(updateRequest.getTotalPages());
        assertThat(existingEntity.getEstimatedReadingMinutes()).isEqualTo(updateRequest.getEstimatedReadingMinutes());
        assertThat(existingEntity.getPublisher()).isEqualTo(updateRequest.getPublisher());
        assertThat(existingEntity.getPublicationYear()).isEqualTo(updateRequest.getPublicationYear());
        assertThat(existingEntity.isPublic()).isEqualTo(updateRequest.getIsPublic());
        assertThat(existingEntity.getDifficultyLevel()).isEqualTo(updateRequest.getDifficultyLevel());

        // These fields should not change during the update
        assertThat(existingEntity.getId()).isEqualTo(LibraryEntityFixtures.DEFAULT_BOOK_ID);
        assertThat(existingEntity.getAddedByUserId()).isEqualTo(LibraryEntityFixtures.DEFAULT_USER_ID);
        assertThat(existingEntity.getIsbn()).isEqualTo("9780743273565");
        assertThat(existingEntity.getCategory()).isEqualTo(BookCategory.FICTION);
    }

    @Test
    @DisplayName("Should handle partial updates without affecting unchanged fields")
    void shouldHandlePartialUpdatesWithoutAffectingUnchangedFields() {
        // Given
        BookEntity existingEntity = LibraryEntityFixtures.createDefaultBookEntity();
        String originalAuthor = existingEntity.getAuthor();
        Integer originalPages = existingEntity.getTotalPages();

        BookUpdateRequest partialUpdate = LibraryRequestFixtures.createPartialUpdateRequest();

        // When
        BookFactory.updateBookEntity(existingEntity, partialUpdate);

        // Then
        assertThat(existingEntity.getTitle()).isEqualTo("Partially Updated Title");
        assertThat(existingEntity.getDescription()).isEqualTo("Only updating title and description");

        // Unchanged fields should remain the same
        assertThat(existingEntity.getAuthor()).isEqualTo(originalAuthor);
        assertThat(existingEntity.getTotalPages()).isEqualTo(originalPages);
    }


    @Test
    @DisplayName("Should create book response from entity")
    void shouldCreateBookResponseFromEntity() {
        // Given
        BookEntity entity = LibraryEntityFixtures.createDefaultBookEntity();
        // Set timestamps that would be set by JPA
        entity.setCreatedAt(java.time.Instant.now().minusSeconds(86400));
        entity.setUpdatedAt(java.time.Instant.now());

        // When
        BookResponse result = BookFactory.createBookResponse(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getTitle()).isEqualTo(entity.getTitle());
        assertThat(result.getAuthor()).isEqualTo(entity.getAuthor());
        assertThat(result.getIsbn()).isEqualTo(entity.getIsbn());
        assertThat(result.getCategory()).isEqualTo(entity.getCategory());
        assertThat(result.getDescription()).isEqualTo(entity.getDescription());
        assertThat(result.getCoverImageUrl()).isEqualTo(entity.getCoverImageUrl());
        assertThat(result.getTotalPages()).isEqualTo(entity.getTotalPages());
        assertThat(result.getEstimatedReadingMinutes()).isEqualTo(entity.getEstimatedReadingMinutes());
        assertThat(result.getLanguage()).isEqualTo(entity.getLanguage());
        assertThat(result.getPublisher()).isEqualTo(entity.getPublisher());
        assertThat(result.getPublicationYear()).isEqualTo(entity.getPublicationYear());
        assertThat(result.getAddedByUserId()).isEqualTo(entity.getAddedByUserId());
        assertThat(result.isPublic()).isEqualTo(entity.isPublic());
        assertThat(result.getDifficultyLevel()).isEqualTo(entity.getDifficultyLevel());
        assertThat(result.getExternalId()).isEqualTo(entity.getExternalId());
        assertThat(result.getExternalSource()).isEqualTo(entity.getExternalSource());
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create book responses from list of entities")
    void shouldCreateBookResponsesFromListOfEntities() {
        // Given
        BookEntity entity1 = LibraryEntityFixtures.createDefaultBookEntity();
        entity1.setCreatedAt(java.time.Instant.now().minusSeconds(86400));
        entity1.setUpdatedAt(java.time.Instant.now());

        BookEntity entity2 = LibraryEntityFixtures.createPublicBookEntity();
        entity2.setCreatedAt(java.time.Instant.now().minusSeconds(86400));
        entity2.setUpdatedAt(java.time.Instant.now());

        List<BookEntity> entities = List.of(entity1, entity2);

        // When
        List<BookResponse> results = BookFactory.createBookResponses(entities);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("The Great Gatsby");
        assertThat(results.get(1).getTitle()).isEqualTo("Public Book");
        assertThat(results.get(0).isPublic()).isFalse();
        assertThat(results.get(1).isPublic()).isTrue();
    }


    @Test
    @DisplayName("Should create empty list when given empty entity list")
    void shouldCreateEmptyListWhenGivenEmptyEntityList() {
        // Given
        List<BookEntity> emptyList = List.of();

        // When
        List<BookResponse> results = BookFactory.createBookResponses(emptyList);

        // Then
        assertThat(results).isEmpty();
    }
}