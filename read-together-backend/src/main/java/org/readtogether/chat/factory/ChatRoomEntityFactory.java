package org.readtogether.chat.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.chat.entity.ChatRoomEntity;
import org.readtogether.chat.common.enums.ChatRoomType;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;

import java.util.UUID;

import static org.readtogether.chat.common.enums.ChatRoomType.DIRECT;
import static org.readtogether.chat.common.enums.ChatRoomType.GROUP;

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
            String roomName) {

        return ChatRoomEntity.builder()
                .name(roomName)
                .type(DIRECT)
                .creatorId(user1Id)
                .isActive(true)
                .maxParticipants(2)
                .build();
    }

    private static ChatRoomType mapToEntityType(
            ChatRoomType requestType) {

        return switch (requestType) {
            case DIRECT -> DIRECT;
            case GROUP -> GROUP;
        };
    }
}