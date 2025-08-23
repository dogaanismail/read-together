package org.readtogether.library.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.library.common.enums.BookCategory;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private UUID id;
    private String title;
    private String author;
    private String isbn;
    private BookCategory category;
    private String description;
    private String coverImageUrl;
    private Integer totalPages;
    private Integer estimatedReadingMinutes;
    private String language;
    private String publisher;
    private Integer publicationYear;
    private UUID addedByUserId;
    private boolean isPublic;
    private Integer difficultyLevel;
    private String externalId;
    private String externalSource;
    private Instant createdAt;
    private Instant updatedAt;
}
