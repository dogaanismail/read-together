package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.security.model.Token;

@UtilityClass
public class TokenFixtures {

    public static Token createToken() {

        return Token.builder()
                .accessToken("access-token")
                .accessTokenExpiresAt(123456789L)
                .refreshToken("refresh-token")
                .build();
    }
}
