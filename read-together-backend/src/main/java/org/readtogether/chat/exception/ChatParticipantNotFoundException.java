package org.readtogether.chat.exception;

import org.readtogether.common.exception.RecordNotFoundException;
import java.io.Serial;

public class ChatParticipantNotFoundException extends RecordNotFoundException {

    @Serial
    private static final long serialVersionUID = -3952215105519401567L;

    private static final String DEFAULT_MESSAGE = """
            Chat participant not found!
            """;

    public ChatParticipantNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ChatParticipantNotFoundException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}