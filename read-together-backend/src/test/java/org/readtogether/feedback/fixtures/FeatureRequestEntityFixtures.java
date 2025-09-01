package org.readtogether.feedback.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.common.enums.Priority;
import org.readtogether.feedback.entity.FeatureRequestEntity;

import java.util.UUID;

@UtilityClass
public class FeatureRequestEntityFixtures {

    public static final UUID DEFAULT_FEATURE_REQUEST_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    public static final UUID DEFAULT_AUTHOR_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

    public static FeatureRequestEntity createDefaultFeatureRequestEntity() {

        return FeatureRequestEntity.builder()
                .id(DEFAULT_FEATURE_REQUEST_ID)
                .title("Dark mode for better accessibility")
                .description("Add a dark theme option to reduce eye strain during long practice sessions.")
                .category(FeatureRequestCategory.UI_UX_IMPROVEMENTS)
                .priority(Priority.MEDIUM)
                .status(FeatureRequestStatus.SUBMITTED)
                .votes(0)
                .authorId(DEFAULT_AUTHOR_ID)
                .build();
    }

    public static FeatureRequestEntity createFeatureRequestEntity(
            String title,
            String description,
            FeatureRequestCategory category,
            Priority priority) {

        return FeatureRequestEntity.builder()
                .title(title)
                .description(description)
                .category(category)
                .priority(priority)
                .status(FeatureRequestStatus.SUBMITTED)
                .votes(0)
                .authorId(DEFAULT_AUTHOR_ID)
                .build();
    }

    public static FeatureRequestEntity createFeatureRequestEntity(
            String title,
            String description,
            FeatureRequestCategory category,
            Priority priority,
            FeatureRequestStatus status,
            int votes) {

        return FeatureRequestEntity.builder()
                .title(title)
                .description(description)
                .category(category)
                .priority(priority)
                .status(status)
                .votes(votes)
                .authorId(DEFAULT_AUTHOR_ID)
                .build();
    }

}