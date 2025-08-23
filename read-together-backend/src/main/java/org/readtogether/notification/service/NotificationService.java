package org.readtogether.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.notification.entity.NotificationEntity;
import org.readtogether.notification.common.enums.NotificationPreferenceType;
import org.readtogether.notification.factory.NotificationEntityFactory;
import org.readtogether.notification.repository.NotificationRepository;
import org.readtogether.notification.utils.NotificationMetadataUtils;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.infrastructure.websocket.service.WebSocketNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketService;
    private final NotificationPreferencesService preferencesService;
    private final NotificationProviderService providerService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void notifySessionUploadStarted(UUID userId, SessionEntity session) {

        if (!preferencesService.shouldSendPushNotification(userId, NotificationPreferenceType.UPLOAD_STATUS)) {
            log.debug("User {} has disabled upload status push notifications", userId);
            return;
        }

        String metadata = NotificationMetadataUtils.createSessionMetadata(objectMapper, session);
        NotificationEntity notification = NotificationEntityFactory.createSessionUploadStartedNotification(userId, session, metadata);

        notificationRepository.save(notification);
        webSocketService.notifySessionStatusChange(userId, session);

        log.info("Notified user {} about session upload start: {}", userId, session.getId());
    }

    @Transactional
    public void notifySessionUploadProgress(
            UUID userId,
            UUID sessionId,
            int progress) {

        webSocketService.notifyUploadProgress(userId, sessionId, progress);

        if (progress == 25 || progress == 50 || progress == 75) {
            String metadata = NotificationMetadataUtils.createProgressMetadata(objectMapper, progress);
            NotificationEntity notification = NotificationEntityFactory.createSessionUploadProgressNotification(userId, sessionId, progress, metadata);

            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void notifySessionUploadCompleted(UUID userId, SessionEntity session) {

        String metadata = NotificationMetadataUtils.createSessionMetadata(objectMapper, session);
        NotificationEntity notification = NotificationEntityFactory.createSessionUploadCompletedNotification(userId, session, metadata);

        notificationRepository.save(notification);

        providerService.sendMultiChannelNotification(
                userId,
                NotificationPreferenceType.UPLOAD_STATUS,
                notification.getTitle(),
                notification.getMessage(),
                metadata
        );

        log.info("Notified user {} about session upload completion: {}", userId, session.getId());
    }

    @Transactional
    public void notifySessionUploadFailed(
            UUID userId,
            SessionEntity session,
            String errorMessage) {

        String metadata = NotificationMetadataUtils.createErrorMetadata(objectMapper, session, errorMessage);
        NotificationEntity notification = NotificationEntityFactory.createSessionUploadFailedNotification(userId, session, errorMessage, metadata);

        notificationRepository.save(notification);
        webSocketService.notifySessionFailed(userId, session, errorMessage);

        providerService.sendMultiChannelNotification(
                userId,
                NotificationPreferenceType.UPLOAD_STATUS,
                notification.getTitle(),
                notification.getMessage(),
                metadata
        );

        log.info("Notified user {} about session upload failure: {}", userId, session.getId());
    }

    @Transactional
    public void notifySessionProcessingStarted(UUID userId, SessionEntity session) {

        String metadata = NotificationMetadataUtils.createSessionMetadata(objectMapper, session);
        NotificationEntity notification = NotificationEntityFactory.createSessionProcessingStartedNotification(userId, session, metadata);

        notificationRepository.save(notification);
        webSocketService.notifySessionStatusChange(userId, session);
    }

    @Transactional
    public void notifySessionLiked(
            UUID sessionOwnerId,
            UUID likerUserId,
            SessionEntity session) {

        if (!preferencesService.shouldSendPushNotification(sessionOwnerId, NotificationPreferenceType.SESSION_LIKES)) {
            log.debug("User {} has disabled session like notifications", sessionOwnerId);
            return;
        }

        String metadata = NotificationMetadataUtils.createLikeMetadata(objectMapper, session, likerUserId);
        NotificationEntity notification = NotificationEntityFactory.createSessionLikedNotification(sessionOwnerId, session, metadata);

        notificationRepository.save(notification);

        // Send notification using new WebSocket service method
        webSocketService.notifySessionLiked(sessionOwnerId, session, likerUserId);

        // Send multi-channel notification
        providerService.sendMultiChannelNotification(
                sessionOwnerId,
                NotificationPreferenceType.SESSION_LIKES,
                notification.getTitle(),
                notification.getMessage(),
                metadata
        );

        log.info("Notified user {} about session like from user {}", sessionOwnerId, likerUserId);
    }

    @Transactional
    public void notifySessionCommented(
            UUID sessionOwnerId,
            UUID commenterUserId,
            SessionEntity session,
            String commentContent) {

        if (!preferencesService.shouldSendPushNotification(sessionOwnerId, NotificationPreferenceType.SESSION_COMMENTS)) {
            log.debug("User {} has disabled session comment notifications", sessionOwnerId);
            return;
        }

        String metadata = NotificationMetadataUtils.createCommentMetadata(objectMapper, session, commenterUserId, commentContent);
        NotificationEntity notification = NotificationEntityFactory.createSessionCommentedNotification(sessionOwnerId, session, metadata);

        notificationRepository.save(notification);

        // Send multi-channel notification
        providerService.sendMultiChannelNotification(
                sessionOwnerId,
                NotificationPreferenceType.SESSION_COMMENTS,
                notification.getTitle(),
                notification.getMessage(),
                metadata
        );

        log.info("Notified user {} about session comment from user {}", sessionOwnerId, commenterUserId);
    }

    @Transactional
    public void notifyNewFollower(
            UUID followedUserId,
            UUID followerUserId,
            String followerUsername) {

        if (!preferencesService.shouldSendPushNotification(followedUserId, NotificationPreferenceType.NEW_FOLLOWERS)) {
            log.debug("User {} has disabled new follower notifications", followedUserId);
            return;
        }

        String metadata = NotificationMetadataUtils.createFollowerMetadata(objectMapper, followerUserId, followerUsername);
        NotificationEntity notification = NotificationEntityFactory.createNewFollowerNotification(followedUserId, followerUsername, metadata);

        notificationRepository.save(notification);

        // Send real-time notification using a new WebSocket service method
        webSocketService.notifyNewFollower(followedUserId, followerUserId, followerUsername);

        // Send multi-channel notification
        providerService.sendMultiChannelNotification(
                followedUserId,
                NotificationPreferenceType.NEW_FOLLOWERS,
                notification.getTitle(),
                notification.getMessage(),
                metadata
        );

        log.info("Notified user {} about new follower {}", followedUserId, followerUserId);
    }

    @Transactional
    public void notifyLiveStreamStarted(
            UUID userId,
            UUID streamerId,
            String streamerUsername,
            String streamTitle) {

        if (!preferencesService.shouldSendPushNotification(userId, NotificationPreferenceType.LIVE_STREAM_ALERTS)) {
            log.debug("User {} has disabled live stream notifications", userId);
            return;
        }

        String metadata = NotificationMetadataUtils.createLiveStreamMetadata(objectMapper, streamerId, streamerUsername, streamTitle);
        NotificationEntity notification = NotificationEntityFactory.createLiveStreamStartedNotification(userId, streamerUsername, streamTitle, metadata);

        notificationRepository.save(notification);

        // Send real-time notification
        webSocketService.notifyLiveStreamStarted(userId, streamerId, streamerUsername, streamTitle);

        // Send multi-channel notification
        providerService.sendMultiChannelNotification(
                userId,
                NotificationPreferenceType.LIVE_STREAM_ALERTS,
                notification.getTitle(),
                notification.getMessage(),
                metadata
        );

        log.info("Notified user {} about live stream from {}", userId, streamerUsername);
    }

    public Page<NotificationEntity> getUserNotifications(
            UUID userId,
            Pageable pageable) {

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long getUnreadCount(UUID userId) {

        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public boolean markAsRead(
            UUID notificationId,
            UUID userId) {

        int updated = notificationRepository.markAsRead(notificationId, userId, Instant.now());
        return updated > 0;
    }

    @Transactional
    public int markAllAsRead(UUID userId) {

        return notificationRepository.markAllAsRead(userId, Instant.now());
    }
}
