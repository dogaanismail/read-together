package org.readtogether.library.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.library.entity.BookEntity;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.response.BookResponse;
import org.readtogether.library.model.request.BookUpdateRequest;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class BookFactory {

    public static BookEntity createBookEntity(
            BookCreateRequest request,
            UUID userId) {

        return BookEntity.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .totalPages(request.getTotalPages())
                .estimatedReadingMinutes(request.getEstimatedReadingMinutes())
                .language(request.getLanguage() != null ? request.getLanguage() : "en")
                .publisher(request.getPublisher())
                .publicationYear(request.getPublicationYear())
                .addedByUserId(userId)
                .isPublic(request.isPublic())
                .difficultyLevel(request.getDifficultyLevel())
                .externalId(request.getExternalId())
                .externalSource(request.getExternalSource())
                .build();
    }

    public static void updateBookEntity(
            BookEntity entity,
            BookUpdateRequest request) {

        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }

        if (request.getAuthor() != null) {
            entity.setAuthor(request.getAuthor());
        }

        if (request.getIsbn() != null) {
            entity.setIsbn(request.getIsbn());
        }

        if (request.getCategory() != null) {
            entity.setCategory(request.getCategory());
        }

        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        if (request.getCoverImageUrl() != null) {
            entity.setCoverImageUrl(request.getCoverImageUrl());
        }

        if (request.getTotalPages() != null) {
            entity.setTotalPages(request.getTotalPages());
        }

        if (request.getEstimatedReadingMinutes() != null) {
            entity.setEstimatedReadingMinutes(request.getEstimatedReadingMinutes());
        }

        if (request.getLanguage() != null) {
            entity.setLanguage(request.getLanguage());
        }

        if (request.getPublisher() != null) {
            entity.setPublisher(request.getPublisher());
        }

        if (request.getPublicationYear() != null) {
            entity.setPublicationYear(request.getPublicationYear());
        }

        if (request.getIsPublic() != null) {
            entity.setPublic(request.getIsPublic());
        }

        if (request.getDifficultyLevel() != null) {
            entity.setDifficultyLevel(request.getDifficultyLevel());
        }

        if (request.getExternalId() != null) {
            entity.setExternalId(request.getExternalId());
        }

        if (request.getExternalSource() != null) {
            entity.setExternalSource(request.getExternalSource());
        }
    }

    public BookResponse createBookResponse(
            BookEntity entity) {

        return BookResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .coverImageUrl(entity.getCoverImageUrl())
                .totalPages(entity.getTotalPages())
                .estimatedReadingMinutes(entity.getEstimatedReadingMinutes())
                .language(entity.getLanguage())
                .publisher(entity.getPublisher())
                .publicationYear(entity.getPublicationYear())
                .addedByUserId(entity.getAddedByUserId())
                .isPublic(entity.isPublic())
                .difficultyLevel(entity.getDifficultyLevel())
                .externalId(entity.getExternalId())
                .externalSource(entity.getExternalSource())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<BookResponse> createBookResponses(
            List<BookEntity> entities) {

        return entities.stream()
                .map(BookFactory::createBookResponse)
                .toList();
    }

}
