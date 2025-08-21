package org.readtogether.chat.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.model.response.ChatMessageResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class ChatWebSocketNotificationFactory {

    public static Map<String, Object> createNewMessageNotification(ChatMessageResponse message) {
        return Map.of(
            "type", "NEW_MESSAGE",
            "message", message,
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createTypingNotification(
            UUID chatRoomId,
            UUID userId,
            String username,
            boolean isTyping) {
        
        return Map.of(
            "type", "TYPING",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "username", username,
            "isTyping", isTyping,
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createUserJoinedNotification(
            UUID chatRoomId,
            UUID userId,
            String username) {
        
        return Map.of(
            "type", "USER_JOINED",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "username", username,
            "message", username + " joined the chat",
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createUserLeftNotification(
            UUID chatRoomId,
            UUID userId,
            String username) {
        
        return Map.of(
            "type", "USER_LEFT",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "username", username,
            "message", username + " left the chat",
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createMessageReadNotification(
            UUID chatRoomId,
            UUID userId,
            UUID messageId) {
        
        return Map.of(
            "type", "MESSAGE_READ",
            "chatRoomId", chatRoomId.toString(),
            "userId", userId.toString(),
            "messageId", messageId.toString(),
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createChatRoomCreatedNotification(
            UUID chatRoomId,
            String roomName,
            UUID creatorId) {
        
        return Map.of(
            "type", "CHAT_ROOM_CREATED",
            "chatRoomId", chatRoomId.toString(),
            "roomName", roomName,
            "creatorId", creatorId.toString(),
            "message", "New chat room created: " + roomName,
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createParticipantAddedNotification(
            UUID chatRoomId,
            UUID participantId,
            String participantName) {
        
        return Map.of(
            "type", "PARTICIPANT_ADDED",
            "chatRoomId", chatRoomId.toString(),
            "participantId", participantId.toString(),
            "participantName", participantName,
            "message", participantName + " was added to the chat",
            "timestamp", System.currentTimeMillis()
        );
    }

    public static Map<String, Object> createErrorNotification(
            String errorType,
            String errorMessage) {
        
        return Map.of(
            "type", "ERROR",
            "errorType", errorType,
            "message", errorMessage,
            "timestamp", System.currentTimeMillis()
        );
    }
}