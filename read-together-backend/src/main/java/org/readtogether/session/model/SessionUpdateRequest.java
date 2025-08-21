package org.readtogether.session.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SessionUpdateRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean isPublic;

    private String tags; // Comma-separated tags
}
