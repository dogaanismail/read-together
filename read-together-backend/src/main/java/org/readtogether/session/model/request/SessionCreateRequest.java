package org.readtogether.session.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.readtogether.session.entity.SessionEntity;

import java.util.UUID;

@Data
public class SessionCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Media type is required")
    private SessionEntity.MediaType mediaType;

    private boolean isPublic = false;

    private UUID readingRoomId;

    private String tags;

    private String bookTitle;

    private String language;

    private boolean isLive = false;

    private String authorName;
}
