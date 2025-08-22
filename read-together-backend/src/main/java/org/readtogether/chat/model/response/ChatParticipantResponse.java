package org.readtogether.chat.model.response;

import lombok.*;
import org.readtogether.chat.entity.enums.ParticipantRole;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private String username;
    private String avatar;
    private ParticipantRole role;
    private Instant joinedAt;
    private Boolean isActive;
    private Integer unreadCount;
    private Instant lastReadAt;
    private Boolean online;
    private String lastSeen;

}