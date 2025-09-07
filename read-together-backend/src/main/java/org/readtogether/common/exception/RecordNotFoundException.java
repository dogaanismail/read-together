package org.readtogether.common.exception;

import java.io.Serial;

/**
 * Base exception for 404 scenarios when a record or resource cannot be found.
 */
public class RecordNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RecordNotFoundException() {
        super("Record not found");
    }

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

