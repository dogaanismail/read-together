package org.readtogether.chat.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;

import java.util.UUID;

@UtilityClass
public class ChatRoomEntityFactory {

    public static ChatRoomEntity createFromRequest(
            ChatRoomCreateRequest request,
            UUID createdBy) {
        
        return ChatRoomEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(mapToEntityType(request.getType()))
                .creatorId(createdBy)
                .isActive(true)
                .maxParticipants(request.getMaxParticipants())
                .build();
    }

    public static ChatRoomEntity createDirectChatRoom(
            UUID user1Id,
            UUID user2Id,
            String roomName) {
        
        return ChatRoomEntity.builder()
                .name(roomName)
                .type(ChatRoomEntity.ChatRoomType.DIRECT)
                .creatorId(user1Id)
                .isActive(true)
                .maxParticipants(2)
                .build();
    }

    private static ChatRoomEntity.ChatRoomType mapToEntityType(ChatRoomCreateRequest.ChatRoomType requestType) {
        return switch (requestType) {
            case DIRECT -> ChatRoomEntity.ChatRoomType.DIRECT;
            case GROUP -> ChatRoomEntity.ChatRoomType.GROUP;
        };
    }
}