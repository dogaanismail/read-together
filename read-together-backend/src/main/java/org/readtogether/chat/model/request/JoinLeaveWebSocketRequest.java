package org.readtogether.chat.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JoinLeaveWebSocketRequest {

    private UUID chatRoomId;

}