package org.readtogether.infrastructure.websocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.infrastructure.websocket.factory.WebSocketNotificationFactory;
import org.readtogether.infrastructure.websocket.util.WebSocketUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifySessionStatusChange(UUID userId, SessionEntity session) {

        Map<String, Object> notification = WebSocketNotificationFactory.createSessionStatusNotification(session);
        WebSocketUtils.sendSessionStatusNotification(messagingTemplate, userId, notification);
    }

    public void notifyUploadProgress(
            UUID userId,
            UUID sessionId,
            int progressPercentage) {

        Map<String, Object> notification = WebSocketNotificationFactory.createUploadProgressNotification(sessionId, progressPercentage);
        WebSocketUtils.sendUploadProgressNotification(messagingTemplate, userId, notification);
    }

    public void notifySessionCompleted(UUID userId, SessionEntity session) {

        Map<String, Object> notification = WebSocketNotificationFactory.createSessionCompletedNotification(session);
        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notification);
    }

    public void notifySessionFailed(
            UUID userId,
            SessionEntity session,
            String errorMessage) {

        Map<String, Object> notification = WebSocketNotificationFactory.createSessionFailedNotification(session, errorMessage);
        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notification);
    }

    public void notifyNewFollower(
            UUID userId,
            UUID followerUserId,
            String followerUsername) {

        Map<String, Object> notification = WebSocketNotificationFactory.createNewFollowerNotification(followerUserId, followerUsername);
        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notification);
    }

    public void notifySessionLiked(
            UUID userId,
            SessionEntity session,
            UUID likerUserId) {

        Map<String, Object> notification = WebSocketNotificationFactory.createSessionLikedNotification(session, likerUserId);
        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notification);
    }

    public void notifyLiveStreamStarted(
            UUID userId,
            UUID streamerId,
            String streamerUsername,
            String streamTitle) {

        Map<String, Object> notification = WebSocketNotificationFactory.createLiveStreamStartedNotification(streamerId, streamerUsername, streamTitle);
        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notification);
    }

    public void sendNotificationToUser(
            UUID userId,
            Map<String, Object> notificationData) {

        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notificationData);
    }

    public void sendCustomNotification(
            UUID userId,
            String type,
            String title,
            String message) {

        Map<String, Object> notification = WebSocketNotificationFactory.createGeneralNotification(type, title, message);
        WebSocketUtils.sendGeneralNotification(messagingTemplate, userId, notification);
    }
}
