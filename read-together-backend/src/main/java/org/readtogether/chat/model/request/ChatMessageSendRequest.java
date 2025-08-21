package org.readtogether.chat.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSendRequest {

    @NotNull(message = "Chat room ID is required")
    private UUID chatRoomId;

    @Size(max = 4000, message = "Message content must not exceed 4000 characters")
    private String content;

    @NotNull(message = "Message type is required")
    private MessageType type;

    private UUID replyToMessageId;

    private MultipartFile attachment;

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        EMOJI
    }
}