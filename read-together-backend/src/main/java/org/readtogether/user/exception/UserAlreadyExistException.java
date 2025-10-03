package org.readtogether.user.exception;

import org.readtogether.common.exception.ConflictException;
import java.io.Serial;

public class UserAlreadyExistException extends ConflictException {

    @Serial
    private static final long serialVersionUID = -2178948664026920647L;

    private static final String DEFAULT_MESSAGE = """
            User already exist!
            """;

    public UserAlreadyExistException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }

}
