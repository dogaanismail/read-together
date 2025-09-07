package org.readtogether.security.exception;

import org.readtogether.common.exception.ConflictException;
import java.io.Serial;

public class TokenAlreadyInvalidatedException extends ConflictException {

    @Serial
    private static final long serialVersionUID = -3922046409563858698L;

    private static final String DEFAULT_MESSAGE = """
            Token is already invalidated!
            """;

    public TokenAlreadyInvalidatedException() {
        super(DEFAULT_MESSAGE);
    }

    public TokenAlreadyInvalidatedException(String tokenId) {
        super(DEFAULT_MESSAGE + " TokenID = " + tokenId);
    }

}
