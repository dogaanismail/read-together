package org.readtogether.common.exception;

import java.io.Serial;

/**
 * Base type for 409 Conflict errors across domains.
 */
public class ConflictException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ConflictException() {
        super("Conflict");
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

