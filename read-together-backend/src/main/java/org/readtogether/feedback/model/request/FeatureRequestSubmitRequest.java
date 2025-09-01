package org.readtogether.feedback.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.Priority;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequestSubmitRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private FeatureRequestCategory category;

    @NotNull(message = "Priority is required")
    private Priority priority;

}