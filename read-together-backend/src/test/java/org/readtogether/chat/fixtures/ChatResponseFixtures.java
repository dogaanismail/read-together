package org.readtogether.chat.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.model.response.ChatMessageResponse;
import org.readtogether.chat.model.response.ChatParticipantResponse;
import org.readtogether.chat.model.response.ChatRoomResponse;
import org.readtogether.chat.common.enums.ChatRoomType;
import org.readtogether.chat.common.enums.MessageType;
import org.readtogether.chat.common.enums.ParticipantRole;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class ChatResponseFixtures {

    public static ChatRoomResponse createChatRoomResponse(
            UUID id,
            String name,
            ChatRoomType type,
            UUID creatorId) {
        
        return ChatRoomResponse.builder()
                .id(id)
                .name(name)
                .description("Test room description")
                .type(type)
                .creatorId(creatorId)
                .isActive(true)
                .maxParticipants(10)
                .unreadCount(0)
                .participants(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static ChatMessageResponse createChatMessageResponse(
            UUID id,
            UUID roomId,
            UUID senderId,
            String content) {
        
        return ChatMessageResponse.builder()
                .id(id)
                .chatRoomId(roomId)
                .senderId(senderId)
                .senderName("Test User")
                .senderUsername("testuser")
                .content(content)
                .messageType(MessageType.TEXT)
                .sentAt(Instant.now())
                .isDeleted(false)
                .build();
    }

    public static ChatParticipantResponse createChatParticipantResponse(
            UUID id,
            UUID userId,
            String userName,
            ParticipantRole role) {
        
        return ChatParticipantResponse.builder()
                .id(id)
                .userId(userId)
                .userName(userName)
                .username("testuser")
                .role(role)
                .joinedAt(Instant.now())
                .isActive(true)
                .unreadCount(0)
                .lastReadAt(Instant.now())
                .online(false)
                .lastSeen("Unknown")
                .build();
    }
}