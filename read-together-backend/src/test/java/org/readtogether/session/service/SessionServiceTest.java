package org.readtogether.session.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.feed.service.FeedService;
import org.readtogether.infrastructure.storage.service.StorageService;
import org.readtogether.notification.service.NotificationService;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.fixtures.SessionEntityFixtures;
import org.readtogether.session.fixtures.SessionRequestFixtures;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.readtogether.session.model.request.SessionUpdateRequest;
import org.readtogether.session.model.response.SessionResponse;
import org.readtogether.session.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.readtogether.session.common.enums.MediaType.AUDIO;
import static org.readtogether.session.common.enums.ProcessingStatus.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private FeedService feedService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SessionService sessionService;

    private UUID userId;
    private SessionEntity defaultSession;
    private SessionCreateRequest defaultCreateRequest;
    private MultipartFile mockFile;
    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        userId = SessionEntityFixtures.DEFAULT_USER_ID;
        defaultSession = SessionEntityFixtures.createDefaultSessionEntity();
        defaultCreateRequest = SessionRequestFixtures.createDefaultCreateSessionRequest();
        mockFile = new MockMultipartFile(
                "file",
                "test-audio.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        // Mock SecurityUtils static call
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(() -> SecurityUtils.getCurrentUserId(authentication)).thenReturn(userId);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    @DisplayName("Should create session synchronously with file upload")
    void shouldCreateSession() {
        // Given
        String uploadedUrl = "https://example.com/uploaded/file.mp3";
        when(storageService.uploadFile(any(MultipartFile.class), anyString(), anyString()))
                .thenReturn(uploadedUrl);
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(defaultSession);

        // When
        SessionResponse result = sessionService.createSession(defaultCreateRequest, mockFile, authentication);

        // Then
        assertThat(result).isNotNull();
        verify(sessionRepository).save(any(SessionEntity.class));
        verify(storageService).uploadFile(eq(mockFile), anyString(), anyString());
        verify(feedService).createFeedItemFromSession(any(SessionEntity.class));

        ArgumentCaptor<SessionEntity> sessionCaptor = ArgumentCaptor.forClass(SessionEntity.class);
        verify(sessionRepository).save(sessionCaptor.capture());

        SessionEntity savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getUserId()).isEqualTo(userId);
        assertThat(savedSession.getTitle()).isEqualTo(defaultCreateRequest.getTitle());
        assertThat(savedSession.getMediaType()).isEqualTo(defaultCreateRequest.getMediaType());
        assertThat(savedSession.getProcessingStatus()).isEqualTo(COMPLETED);
    }

    @Test
    @DisplayName("Should create session asynchronously")
    void shouldCreateSessionAsync() {
        // Given
        String uploadedUrl = "https://example.com/uploaded/file.mp3";
        SessionEntity pendingSession = SessionEntityFixtures.createPendingSessionEntity();
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(pendingSession);
        when(storageService.uploadFileAsync(any(MultipartFile.class), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(uploadedUrl));
        when(sessionRepository.findById(any(UUID.class))).thenReturn(Optional.of(pendingSession));

        // When
        CompletableFuture<SessionResponse> result = sessionService.createSessionAsync(
                defaultCreateRequest, mockFile, authentication);

        // Then
        assertThat(result).isNotNull();
        verify(sessionRepository, atLeastOnce()).save(any(SessionEntity.class));
        verify(notificationService).notifySessionUploadStarted(eq(userId), any(SessionEntity.class));
    }

    @Test
    @DisplayName("Should retrieve session by ID")
    void shouldRetrieveSessionById() {
        // Given
        UUID sessionId = defaultSession.getId();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(defaultSession));

        // When
        Optional<SessionResponse> result = sessionService.getSessionById(sessionId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(sessionId);
        assertThat(result.get().getTitle()).isEqualTo(defaultSession.getTitle());
        verify(sessionRepository).findById(sessionId);
    }

    @Test
    @DisplayName("Should return empty when session not found by ID")
    void shouldReturnEmptyWhenSessionNotFound() {
        // Given
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When
        Optional<SessionResponse> result = sessionService.getSessionById(sessionId);

        // Then
        assertThat(result).isEmpty();
        verify(sessionRepository).findById(sessionId);
    }

    @Test
    @DisplayName("Should get user sessions with pagination")
    void shouldGetUserSessionsWithPagination() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<SessionEntity> sessions = List.of(defaultSession);
        Page<SessionEntity> sessionPage = new PageImpl<>(sessions, pageable, 1);

        when(sessionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(sessionPage);

        // When
        Page<SessionResponse> result = sessionService.getUserSessions(page, size, authentication);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(defaultSession.getId());
        verify(sessionRepository).findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Test
    @DisplayName("Should get public sessions")
    void shouldGetPublicSessions() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        SessionEntity publicSession = SessionEntityFixtures.createPublicVideoSessionEntity();
        List<SessionEntity> sessions = List.of(publicSession);
        Page<SessionEntity> sessionPage = new PageImpl<>(sessions, pageable, 1);

        when(sessionRepository.findByIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(COMPLETED, pageable))
                .thenReturn(sessionPage);

        // When
        Page<SessionResponse> result = sessionService.getPublicSessions(page, size);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().isPublic()).isTrue();
        verify(sessionRepository).findByIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(COMPLETED, pageable);
    }

    @Test
    @DisplayName("Should update session")
    void shouldUpdateSession() {
        // Given
        UUID sessionId = defaultSession.getId();
        SessionUpdateRequest updateRequest = SessionRequestFixtures.createDefaultUpdateSessionRequest();
        SessionEntity updatedSession = SessionEntityFixtures.createDefaultSessionEntity();
        updatedSession.setTitle(updateRequest.getTitle());

        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(defaultSession));
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(updatedSession);

        // When
        SessionResponse result = sessionService.updateSession(sessionId, updateRequest, authentication);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(updateRequest.getTitle());
        verify(sessionRepository).findByIdAndUserId(sessionId, userId);
        verify(sessionRepository).save(any(SessionEntity.class));
    }

    @Test
    @DisplayName("Should throw when session not found for update")
    void shouldThrowWhenSessionNotFoundForUpdate() {
        // Given
        UUID sessionId = UUID.randomUUID();
        SessionUpdateRequest updateRequest = SessionRequestFixtures.createDefaultUpdateSessionRequest();

        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> sessionService.updateSession(sessionId, updateRequest, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Session not found");

        verify(sessionRepository).findByIdAndUserId(sessionId, userId);
        verify(sessionRepository, never()).save(any(SessionEntity.class));
    }

    @Test
    @DisplayName("Should delete session and associated file")
    void shouldDeleteSession() {
        // Given
        UUID sessionId = defaultSession.getId();
        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(defaultSession));

        // When
        sessionService.deleteSession(sessionId, authentication);

        // Then
        verify(sessionRepository).findByIdAndUserId(sessionId, userId);
        verify(storageService).deleteFile(defaultSession.getMediaUrl());
        verify(sessionRepository).delete(defaultSession);
    }

    @Test
    @DisplayName("Should throw when session not found for deletion")
    void shouldThrowWhenSessionNotFoundForDeletion() {
        // Given
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> sessionService.deleteSession(sessionId, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Session not found");

        verify(sessionRepository).findByIdAndUserId(sessionId, userId);
        verify(storageService, never()).deleteFile(anyString());
        verify(sessionRepository, never()).delete(any(SessionEntity.class));
    }

    @Test
    @DisplayName("Should process upload with file type validation")
    void shouldProcessUploadWithValidation() {
        // Given
        MultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "audio content".getBytes()
        );
        SessionCreateRequest audioRequest = SessionRequestFixtures.createCreateSessionRequest(
                "Audio Session", "Test audio", AUDIO, false
        );
        
        when(storageService.uploadFile(any(MultipartFile.class), anyString(), anyString()))
                .thenReturn("https://example.com/audio.mp3");
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(defaultSession);

        // When
        SessionResponse result = sessionService.createSession(audioRequest, audioFile, authentication);

        // Then
        assertThat(result).isNotNull();
        verify(sessionRepository).save(any(SessionEntity.class));
    }

    @Test
    @DisplayName("Should reject invalid file type")
    void shouldRejectInvalidFileType() {
        // Given
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "invalid content".getBytes()
        );
        SessionCreateRequest audioRequest = SessionRequestFixtures.createCreateSessionRequest(
                "Audio Session", "Test audio", AUDIO, false
        );

        // When / Then
        assertThatThrownBy(() -> sessionService.createSession(audioRequest, invalidFile, authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid audio file type");

        verify(sessionRepository, never()).save(any(SessionEntity.class));
    }

    @Test
    @DisplayName("Should get user session by ID and user ID")
    void shouldGetUserSession() {
        // Given
        UUID sessionId = defaultSession.getId();
        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(defaultSession));

        // When
        Optional<SessionResponse> result = sessionService.getUserSession(sessionId, authentication);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(sessionId);
        assertThat(result.get().getUserId()).isEqualTo(userId);
        verify(sessionRepository).findByIdAndUserId(sessionId, userId);
    }

    @Test
    @DisplayName("Should return empty when user session not found")
    void shouldReturnEmptyWhenUserSessionNotFound() {
        // Given
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.empty());

        // When
        Optional<SessionResponse> result = sessionService.getUserSession(sessionId, authentication);

        // Then
        assertThat(result).isEmpty();
        verify(sessionRepository).findByIdAndUserId(sessionId, userId);
    }
}