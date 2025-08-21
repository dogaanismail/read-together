package org.readtogether.chat.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatParticipantEntity;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class ChatParticipantEntityFactory {

    public static ChatParticipantEntity createParticipant(
            UUID chatRoomId,
            UUID userId,
            ChatParticipantEntity.ParticipantRole role) {
        
        return ChatParticipantEntity.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .role(role)
                .joinedAt(Instant.now())
                .isActive(true)
                .unreadCount(0)
                .lastReadAt(Instant.now())
                .build();
    }

    public static ChatParticipantEntity createMember(UUID chatRoomId, UUID userId) {
        return createParticipant(chatRoomId, userId, ChatParticipantEntity.ParticipantRole.MEMBER);
    }

    public static ChatParticipantEntity createAdmin(UUID chatRoomId, UUID userId) {
        return createParticipant(chatRoomId, userId, ChatParticipantEntity.ParticipantRole.ADMIN);
    }
}