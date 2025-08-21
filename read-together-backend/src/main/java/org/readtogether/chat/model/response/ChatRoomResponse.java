package org.readtogether.chat.model.response;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private UUID id;
    private String name;
    private String description;
    private ChatRoomType type;
    private UUID creatorId;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isActive;
    private Integer maxParticipants;
    private List<ChatParticipantResponse> participants;
    private ChatMessageResponse lastMessage;
    private Integer unreadCount;

    public enum ChatRoomType {
        DIRECT,
        GROUP
    }
}