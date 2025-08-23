package org.readtogether.chat.exception;

import java.io.Serial;

public class InvalidFileException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3952215105519401568L;

    private static final String DEFAULT_MESSAGE = """
            Invalid file!
            """;

    public InvalidFileException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidFileException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}