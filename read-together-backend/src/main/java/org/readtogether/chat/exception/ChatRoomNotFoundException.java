package org.readtogether.chat.exception;

import java.io.Serial;

public class ChatRoomNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3952215105519401566L;

    private static final String DEFAULT_MESSAGE = """
            Chat room not found!
            """;

    public ChatRoomNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ChatRoomNotFoundException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}