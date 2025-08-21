package org.readtogether.chat.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {

    @NotBlank(message = "Chat room name is required")
    @Size(max = 255, message = "Chat room name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Chat room type is required")
    private ChatRoomType type;

    private List<UUID> participantIds;

    private Integer maxParticipants;

    public enum ChatRoomType {
        DIRECT,
        GROUP
    }
}