package org.readtogether.chat.exception;

import org.readtogether.common.exception.BadRequestException;
import java.io.Serial;

public class InvalidFileException extends BadRequestException {

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