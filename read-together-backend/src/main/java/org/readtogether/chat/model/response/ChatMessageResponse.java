package org.readtogether.chat.model.response;

import lombok.*;
import org.readtogether.chat.common.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private UUID id;

    private UUID chatRoomId;

    private UUID senderId;

    private String senderName;

    private String senderUsername;

    private String senderAvatar;

    private String content;

    private MessageType messageType;

    private Instant sentAt;

    private Instant editedAt;

    private Boolean isDeleted;

    private UUID replyToMessageId;

    private ChatMessageResponse replyToMessage;

    private String attachmentUrl;

    private String attachmentName;

    private Long attachmentSize;

    private String attachmentType;
}