package org.readtogether.session.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.utils.SecurityUtils;
import org.readtogether.common.utils.StoragePathUtils;
import org.readtogether.feed.service.FeedService;
import org.readtogether.infrastructure.storage.service.StorageService;
import org.readtogether.notification.service.NotificationService;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.factory.SessionEntityFactory;
import org.readtogether.session.factory.SessionResponseFactory;
import org.readtogether.session.model.SessionCreateRequest;
import org.readtogether.session.model.SessionResponse;
import org.readtogether.session.model.SessionUpdateRequest;
import org.readtogether.session.repository.SessionRepository;
import org.readtogether.session.utils.SessionValidationUtils;
import org.readtogether.session.utils.SessionUpdateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final StorageService storageService;
    private final FeedService feedService;
    private final NotificationService notificationService;

    @Transactional
    public CompletableFuture<SessionResponse> createSessionAsync(
            SessionCreateRequest request,
            MultipartFile file,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        SessionValidationUtils.validateFileType(file, request.getMediaType());

        SessionEntity session = SessionEntityFactory.createPendingSession(userId, request, file);
        session = sessionRepository.save(session);

        // Notify user that upload has started
        notificationService.notifySessionUploadStarted(userId, session);

        final UUID sessionId = session.getId();
        return processFileUploadAsync(sessionId, file, userId)
                .thenApply(mediaUrl -> {
                    SessionEntity updatedSession = updateSessionAfterUpload(sessionId, mediaUrl);

                    if (updatedSession.getProcessingStatus() == SessionEntity.ProcessingStatus.COMPLETED) {
                        // Notify user that upload is complete
                        notificationService.notifySessionUploadCompleted(userId, updatedSession);
                        createFeedItemForSession(updatedSession);
                    }

                    return SessionResponseFactory.createFromEntity(updatedSession);
                })
                .exceptionally(ex -> {
                    log.error("Failed to upload file for session {}", sessionId, ex);
                    SessionEntity failedSession = markSessionAsFailed(sessionId, ex.getMessage());

                    // Notify user about the failure
                    notificationService.notifySessionUploadFailed(userId, failedSession, ex.getMessage());

                    throw new RuntimeException("Failed to upload session file", ex);
                });
    }

    public SessionResponse createSession(
            SessionCreateRequest request,
            MultipartFile file,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        SessionValidationUtils.validateFileType(file, request.getMediaType());

        SessionEntity session = SessionEntityFactory.createFromRequest(userId, request, file);

        String mediaUrl = uploadFile(file);
        session.setMediaUrl(mediaUrl);
        session.setProcessingStatus(SessionEntity.ProcessingStatus.COMPLETED);

        session = sessionRepository.save(session);

        createFeedItemForSession(session);

        return SessionResponseFactory.createFromEntity(session);
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getUserSessions(
            int page,
            int size,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Pageable pageable = PageRequest.of(page, size);
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(SessionResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getPublicSessions(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);
        return sessionRepository.findByIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
                SessionEntity.ProcessingStatus.COMPLETED, pageable
        ).map(SessionResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getFeed(
            int page,
            int size,
            String mediaType,
            String search) {

        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.trim().isEmpty()) {
            return searchSessions(search.trim(), pageable);
        } else if (mediaType != null) {
            SessionEntity.MediaType type = SessionEntity.MediaType.valueOf(mediaType.toUpperCase());
            return getSessionsByMediaType(type, pageable);
        } else {
            return getPublicSessions(page, size);
        }
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getSessionsByMediaType(
            SessionEntity.MediaType mediaType,
            Pageable pageable) {

        return sessionRepository.findByMediaTypeAndIsPublicTrueAndProcessingStatusOrderByCreatedAtDesc(
                mediaType, SessionEntity.ProcessingStatus.COMPLETED, pageable
        ).map(SessionResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> searchSessions(
            String query,
            Pageable pageable) {

        return sessionRepository.searchPublicSessions(
                query, SessionEntity.ProcessingStatus.COMPLETED, pageable
        ).map(SessionResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Optional<SessionResponse> getUserSession(
            UUID sessionId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        return sessionRepository.findByIdAndUserId(sessionId, userId)
                .map(SessionResponseFactory::createFromEntity);
    }

    @Transactional
    public SessionResponse updateSession(
            UUID sessionId,
            SessionUpdateRequest request,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        SessionEntity session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionUpdateUtils.applyUpdates(session, request);

        session = sessionRepository.save(session);
        return SessionResponseFactory.createFromEntity(session);
    }

    @Transactional
    public void deleteSession(
            UUID sessionId,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        SessionEntity session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        try {
            storageService.deleteFile(session.getMediaUrl());
        } catch (Exception e) {
            log.warn("Failed to delete file from storage: {}", session.getMediaUrl(), e);
        }

        sessionRepository.delete(session);
    }

    @Transactional
    public void incrementViewCount(UUID sessionId) {
        sessionRepository.incrementViewCount(sessionId);
    }

    @Transactional
    public void likeSession(UUID sessionId) {
        sessionRepository.incrementLikeCount(sessionId);
    }

    @Transactional
    public void unlikeSession(UUID sessionId) {
        sessionRepository.decrementLikeCount(sessionId);
    }

    @Async
    public CompletableFuture<String> processFileUploadAsync(
            UUID sessionId,
            MultipartFile file,
            UUID userId) {

        try {
            updateProcessingStatus(sessionId, SessionEntity.ProcessingStatus.PROCESSING);

            // Notify user that processing has started
            SessionEntity session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            notificationService.notifySessionProcessingStarted(userId, session);

            String fileName = StoragePathUtils.generateFileName(file.getOriginalFilename());
            String folder = StoragePathUtils.generateDateFolder("sessions");

            return storageService.uploadFileAsync(file, fileName, folder);
        } catch (Exception e) {
            log.error("Error processing file upload for session {}", sessionId, e);
            throw new RuntimeException("Failed to process file upload", e);
        }
    }

    public Optional<SessionResponse> getSessionById(UUID sessionId) {

        return sessionRepository.findById(sessionId)
                .map(SessionResponseFactory::createFromEntity);
    }

    private String uploadFile(MultipartFile file) {

        String fileName = StoragePathUtils.generateFileName(file.getOriginalFilename());
        String folder = StoragePathUtils.generateDateFolder("sessions");
        return storageService.uploadFile(file, fileName, folder);
    }

    private SessionEntity updateSessionAfterUpload(
            UUID sessionId,
            String mediaUrl) {

        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setMediaUrl(mediaUrl);
        session.setProcessingStatus(SessionEntity.ProcessingStatus.COMPLETED);
        session.setProcessingError(null);

        return sessionRepository.save(session);
    }

    private SessionEntity markSessionAsFailed(
            UUID sessionId,
            String error) {

        return sessionRepository.findById(sessionId)
                .map(session -> {
                    session.setProcessingStatus(SessionEntity.ProcessingStatus.FAILED);
                    session.setProcessingError(error);
                    return sessionRepository.save(session);
                })
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    private void updateProcessingStatus(
            UUID sessionId,
            SessionEntity.ProcessingStatus status) {

        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setProcessingStatus(status);
            sessionRepository.save(session);
        });
    }

    private void createFeedItemForSession(SessionEntity session) {

        try {
            if (session.isPublic()) {
                feedService.createFeedItemFromSession(session);
                log.info("Created feed item for session: {}", session.getId());
            }
        } catch (Exception e) {
            log.error("Failed to create feed item for session: {}", session.getId(), e);
            // Don't fail the session creation if feed item creation fails
        }
    }
}
