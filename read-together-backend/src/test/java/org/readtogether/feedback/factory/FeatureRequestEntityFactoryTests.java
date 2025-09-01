package org.readtogether.feedback.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.readtogether.feedback.fixtures.RequestFixtures;
import org.readtogether.feedback.model.request.FeatureRequestSubmitRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.feedback.common.enums.FeatureRequestCategory.AI_ANALYTICS;
import static org.readtogether.feedback.common.enums.FeatureRequestStatus.SUBMITTED;
import static org.readtogether.feedback.common.enums.Priority.HIGH;

@Tag("unit")
@DisplayName("FeatureRequestEntityFactory Tests")
class FeatureRequestEntityFactoryTests {

    private static final UUID TEST_AUTHOR_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    @Test
    @DisplayName("Should create FeatureRequestEntity from FeatureRequestSubmitRequest")
    void shouldCreateFeatureRequestEntityFromRequest() {

        // Given
        FeatureRequestSubmitRequest request = RequestFixtures.createDefaultFeatureRequestSubmitRequest();

        // When
        FeatureRequestEntity entity = FeatureRequestEntityFactory.createFeatureRequestEntity(request, TEST_AUTHOR_ID);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo(request.getTitle());
        assertThat(entity.getDescription()).isEqualTo(request.getDescription());
        assertThat(entity.getCategory()).isEqualTo(request.getCategory());
        assertThat(entity.getPriority()).isEqualTo(request.getPriority());
        assertThat(entity.getStatus()).isEqualTo(SUBMITTED);
        assertThat(entity.getVotes()).isEqualTo(0);
        assertThat(entity.getAuthorId()).isEqualTo(TEST_AUTHOR_ID);
    }

    @Test
    @DisplayName("Should create FeatureRequestEntity with custom values")
    void shouldCreateFeatureRequestEntityWithCustomValues() {

        // Given
        FeatureRequestSubmitRequest request = RequestFixtures.createFeatureRequestSubmitRequest(
                "AI-powered pronunciation feedback",
                "Integrate AI to provide real-time feedback on pronunciation and speech patterns.",
                AI_ANALYTICS,
                HIGH
        );

        // When
        FeatureRequestEntity entity = FeatureRequestEntityFactory.createFeatureRequestEntity(request, TEST_AUTHOR_ID);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("AI-powered pronunciation feedback");
        assertThat(entity.getDescription()).contains("AI to provide real-time feedback");
        assertThat(entity.getCategory()).isEqualTo(AI_ANALYTICS);
        assertThat(entity.getPriority()).isEqualTo(HIGH);
        assertThat(entity.getStatus()).isEqualTo(SUBMITTED);
        assertThat(entity.getVotes()).isEqualTo(0);
        assertThat(entity.getAuthorId()).isEqualTo(TEST_AUTHOR_ID);
    }

}