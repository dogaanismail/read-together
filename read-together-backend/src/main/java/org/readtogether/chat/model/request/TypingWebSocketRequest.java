package org.readtogether.chat.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TypingWebSocketRequest {

    private UUID chatRoomId;

    private boolean typing;
}