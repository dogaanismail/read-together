package org.readtogether.library.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.library.common.enums.BookCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {

    @Size(max = 300, message = "Title must not exceed 300 characters")
    private String title;

    @Size(max = 200, message = "Author must not exceed 200 characters")
    private String author;

    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;

    private BookCategory category;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageUrl;

    private Integer totalPages;

    private Integer estimatedReadingMinutes;

    @Size(max = 10, message = "Language must not exceed 10 characters")
    private String language;

    @Size(max = 200, message = "Publisher must not exceed 200 characters")
    private String publisher;

    private Integer publicationYear;

    private Boolean isPublic;

    private Integer difficultyLevel;

    @Size(max = 100, message = "External ID must not exceed 100 characters")
    private String externalId;

    @Size(max = 50, message = "External source must not exceed 50 characters")
    private String externalSource;
}
