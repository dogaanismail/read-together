package org.readtogether.user.exception;

import org.readtogether.common.exception.BadRequestException;
import java.io.Serial;

public class UserStatusNotValidException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 3440177088502218750L;

    private static final String DEFAULT_MESSAGE = """
            User status is not valid!
            """;

    public UserStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    public UserStatusNotValidException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
