package org.readtogether.readingroom.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReadingRoomRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 2, message = "Room must allow at least 2 participants")
    @Max(value = 12, message = "Room cannot exceed 50 participants")
    @Builder.Default
    private int maxParticipants = 12;

    @Builder.Default
    private boolean isPublic = true;

    private Instant scheduledStartTime;
}
