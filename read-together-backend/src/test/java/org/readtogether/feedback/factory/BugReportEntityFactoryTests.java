package org.readtogether.feedback.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.feedback.common.enums.BugReportSeverity;
import org.readtogether.feedback.common.enums.BugReportStatus;
import org.readtogether.feedback.entity.BugReportEntity;
import org.readtogether.feedback.fixtures.RequestFixtures;
import org.readtogether.feedback.model.request.BugReportSubmitRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BugReportEntityFactory Tests")
class BugReportEntityFactoryTests {

    private static final UUID TEST_REPORTER_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

    @Test
    @DisplayName("Should create BugReportEntity from BugReportSubmitRequest")
    void shouldCreateBugReportEntityFromRequest() {

        // Given
        BugReportSubmitRequest request = RequestFixtures.createDefaultBugReportSubmitRequest();

        // When
        BugReportEntity entity = BugReportEntityFactory.createBugReportEntity(request, TEST_REPORTER_ID);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo(request.getTitle());
        assertThat(entity.getSeverity()).isEqualTo(request.getSeverity());
        assertThat(entity.getStepsToReproduce()).isEqualTo(request.getStepsToReproduce());
        assertThat(entity.getExpectedVsActualBehavior()).isEqualTo(request.getExpectedVsActualBehavior());
        assertThat(entity.getBrowserDeviceInfo()).isEqualTo(request.getBrowserDeviceInfo());
        assertThat(entity.getStatus()).isEqualTo(BugReportStatus.SUBMITTED);
        assertThat(entity.getReporterId()).isEqualTo(TEST_REPORTER_ID);
    }

    @Test
    @DisplayName("Should create BugReportEntity with custom values")
    void shouldCreateBugReportEntityWithCustomValues() {

        // Given
        BugReportSubmitRequest request = RequestFixtures.createBugReportSubmitRequest(
                "Audio not working in reading rooms",
                BugReportSeverity.CRITICAL,
                "1. Join a reading room 2. Try to enable audio 3. No sound is heard",
                "Expected: Audio should work. Actual: No audio output."
        );

        // When
        BugReportEntity entity = BugReportEntityFactory.createBugReportEntity(request, TEST_REPORTER_ID);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Audio not working in reading rooms");
        assertThat(entity.getSeverity()).isEqualTo(BugReportSeverity.CRITICAL);
        assertThat(entity.getStepsToReproduce()).contains("Join a reading room");
        assertThat(entity.getExpectedVsActualBehavior()).contains("Expected: Audio should work");
        assertThat(entity.getStatus()).isEqualTo(BugReportStatus.SUBMITTED);
        assertThat(entity.getReporterId()).isEqualTo(TEST_REPORTER_ID);
    }

    @Test
    @DisplayName("Should create BugReportEntity without browser info")
    void shouldCreateBugReportEntityWithoutBrowserInfo() {

        // Given
        BugReportSubmitRequest request = BugReportSubmitRequest.builder()
                .title("UI issue")
                .severity(BugReportSeverity.LOW)
                .stepsToReproduce("Steps")
                .expectedVsActualBehavior("Behavior")
                .browserDeviceInfo(null)
                .build();

        // When
        BugReportEntity entity = BugReportEntityFactory.createBugReportEntity(request, TEST_REPORTER_ID);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBrowserDeviceInfo()).isNull();
        assertThat(entity.getReporterId()).isEqualTo(TEST_REPORTER_ID);
    }

}