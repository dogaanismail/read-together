package org.readtogether.chat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageWebSocketRequest {

    @NotNull(message = "Chat room ID is required")
    private UUID chatRoomId;

    private String content;

    @NotNull(message = "Message type is required")
    private MessageType type;

    private UUID replyToMessageId;

    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String attachmentType;

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        EMOJI
    }
}