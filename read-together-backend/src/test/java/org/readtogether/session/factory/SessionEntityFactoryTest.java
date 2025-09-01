package org.readtogether.session.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.fixtures.SessionRequestFixtures;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.session.common.enums.MediaType.AUDIO;
import static org.readtogether.session.common.enums.MediaType.VIDEO;
import static org.readtogether.session.common.enums.ProcessingStatus.PENDING;

@Tag("unit")
@DisplayName("SessionEntityFactory Tests")
class SessionEntityFactoryTest {

    @Test
    @DisplayName("Should create session entity from create request")
    void shouldCreateSessionEntityFromCreateRequest() {
        // Given
        UUID userId = UUID.randomUUID();
        SessionCreateRequest request = SessionRequestFixtures.createDefaultCreateSessionRequest();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-audio.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        // When
        SessionEntity result = SessionEntityFactory.createFromRequest(userId, request, file);

        // Then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
        assertThat(result.getMediaType()).isEqualTo(request.getMediaType());
        assertThat(result.getMimeType()).isEqualTo(file.getContentType());
        assertThat(result.getFileSizeBytes()).isEqualTo(file.getSize());
        assertThat(result.isPublic()).isEqualTo(request.isPublic());
        assertThat(result.getReadingRoomId()).isEqualTo(request.getReadingRoomId());
        assertThat(result.getTags()).isEqualTo(request.getTags());
    }

    @Test
    @DisplayName("Should create pending session with default processing status")
    void shouldCreatePendingSessionWithDefaultStatus() {
        // Given
        UUID userId = UUID.randomUUID();
        SessionCreateRequest request = SessionRequestFixtures.createPublicVideoCreateRequest();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        // When
        SessionEntity result = SessionEntityFactory.createPendingSession(userId, request, file);

        // Then
        assertThat(result.getProcessingStatus()).isEqualTo(PENDING);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getMediaType()).isEqualTo(VIDEO);
        assertThat(result.isPublic()).isTrue();
    }

    @Test
    @DisplayName("Should default unset fields when creating from request")
    void shouldDefaultUnsetFields() {
        // Given
        UUID userId = UUID.randomUUID();
        SessionCreateRequest request = new SessionCreateRequest();
        request.setTitle("Minimal Session");
        request.setDescription(null); // explicitly null
        request.setMediaType(AUDIO);
        request.setPublic(false);
        // Leave readingRoomId and tags unset (null)
        
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "content".getBytes()
        );

        // When
        SessionEntity result = SessionEntityFactory.createFromRequest(userId, request, file);

        // Then
        assertThat(result.getTitle()).isEqualTo("Minimal Session");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getMediaType()).isEqualTo(AUDIO);
        assertThat(result.isPublic()).isFalse();
        assertThat(result.getReadingRoomId()).isNull();
        assertThat(result.getTags()).isNull();
    }

    @Test
    @DisplayName("Should handle video file type correctly")
    void shouldHandleVideoFileTypeCorrectly() {
        // Given
        UUID userId = UUID.randomUUID();
        SessionCreateRequest request = SessionRequestFixtures.createCreateSessionRequest(
                "Video Session",
                "Test video session",
                VIDEO,
                true
        );
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "large video content".getBytes()
        );

        // When
        SessionEntity result = SessionEntityFactory.createFromRequest(userId, request, file);

        // Then
        assertThat(result.getMediaType()).isEqualTo(VIDEO);
        assertThat(result.getMimeType()).isEqualTo("video/mp4");
        assertThat(result.getFileSizeBytes()).isEqualTo(file.getSize());
        assertThat(result.isPublic()).isTrue();
    }

    @Test
    @DisplayName("Should preserve all request fields in pending session")
    void shouldPreserveAllRequestFieldsInPendingSession() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID readingRoomId = UUID.randomUUID();
        SessionCreateRequest request = SessionRequestFixtures.createCreateSessionRequest(
                "Complete Session",
                "Complete description",
                VIDEO,
                true,
                readingRoomId,
                "complete,test,tags"
        );
        MultipartFile file = new MockMultipartFile(
                "file",
                "complete.mp4",
                "video/mp4",
                "complete content".getBytes()
        );

        // When
        SessionEntity result = SessionEntityFactory.createPendingSession(userId, request, file);

        // Then
        assertThat(result.getProcessingStatus()).isEqualTo(PENDING);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo("Complete Session");
        assertThat(result.getDescription()).isEqualTo("Complete description");
        assertThat(result.getMediaType()).isEqualTo(VIDEO);
        assertThat(result.isPublic()).isTrue();
        assertThat(result.getReadingRoomId()).isEqualTo(readingRoomId);
        assertThat(result.getTags()).isEqualTo("complete,test,tags");
        assertThat(result.getMimeType()).isEqualTo("video/mp4");
        assertThat(result.getFileSizeBytes()).isEqualTo(file.getSize());
    }
}