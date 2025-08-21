package org.readtogether.chat.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReadWebSocketRequest {
    private UUID chatRoomId;
    private UUID messageId;
}