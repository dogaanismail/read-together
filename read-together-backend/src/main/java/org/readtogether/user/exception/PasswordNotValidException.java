package org.readtogether.user.exception;

import org.readtogether.common.exception.BadRequestException;
import java.io.Serial;

public class PasswordNotValidException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 7389659106153108528L;

    private static final String DEFAULT_MESSAGE = """
            Password is not valid!
            """;

    public PasswordNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    public PasswordNotValidException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
