package org.readtogether.library.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.library.common.enums.BookStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookProgressUpdateRequest {

    private BookStatus status;

    @Min(value = 0, message = "Current page must be at least 0")
    private Integer currentPage;

    @Min(value = 0, message = "Progress percentage must be at least 0")
    @Max(value = 100, message = "Progress percentage must not exceed 100")
    private Integer progressPercentage;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    @Size(max = 2000, message = "Favorite quotes must not exceed 2000 characters")
    private String favoriteQuotes;

    @Min(value = 1, message = "Personal rating must be at least 1")
    @Max(value = 5, message = "Personal rating must not exceed 5")
    private Integer personalRating;

    @Min(value = 1, message = "Reading goal must be at least 1 page per day")
    private Integer readingGoalPagesPerDay;

    private Boolean isFavorite;
}
