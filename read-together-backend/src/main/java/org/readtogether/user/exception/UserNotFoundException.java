package org.readtogether.user.exception;

import org.readtogether.common.exception.RecordNotFoundException;
import java.io.Serial;

public class UserNotFoundException extends RecordNotFoundException {

    @Serial
    private static final long serialVersionUID = -3952215105519401565L;

    private static final String DEFAULT_MESSAGE = """
            User not found!
            """;

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}
