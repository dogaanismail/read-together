package org.readtogether.security.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.security.model.request.TokenRefreshRequest;

@UtilityClass
public class TokenRequestFixtures {

    public static TokenRefreshRequest createTokenRefreshRequest(
            String refreshToken) {

        return TokenRefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();
    }

    public static TokenRefreshRequest createTokenRefreshRequestWithValidToken() {

        String refreshToken = TokenFixtures.createRefreshToken("test-user-id", KeyFixtures.generateTestRsaKeyPair().getPrivate());
        return TokenRefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();
    }

    public static TokenRefreshRequest createTokenRefreshRequestWithExpiredToken() {

        String expiredToken = TokenFixtures.createExpiredToken(KeyFixtures.generateTestRsaKeyPair().getPrivate());
        return TokenRefreshRequest.builder()
                .refreshToken(expiredToken)
                .build();
    }
}