package org.readtogether.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.infrastructure.config.TokenConfigurationParameter;
import org.readtogether.security.common.enums.TokenClaims;
import org.readtogether.security.fixtures.KeyFixtures;
import org.readtogether.security.fixtures.TokenFixtures;
import org.readtogether.security.model.Token;
import org.readtogether.user.common.enums.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService Tests")
class TokenServiceTests {

    @Mock
    private TokenConfigurationParameter tokenConfigurationParameter;

    @Mock
    private InvalidTokenService invalidTokenService;

    private TokenService tokenService;
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @BeforeEach
    void setUp() {
        keyPair = KeyFixtures.generateTestRsaKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        
        when(tokenConfigurationParameter.getAccessTokenExpireMinute()).thenReturn(30);
        when(tokenConfigurationParameter.getRefreshTokenExpireDay()).thenReturn(7);
        
        tokenService = new TokenService(tokenConfigurationParameter, invalidTokenService, publicKey, privateKey);
    }

    @Test
    @DisplayName("Should verify and validate valid token")
    void shouldVerifyAndValidateValidToken() {
        // Given
        String userId = UUID.randomUUID().toString();
        Date futureDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1 hour from now
        String validToken = TokenFixtures.createValidAccessToken(userId, Set.of(UserType.USER), futureDate);
        
        // We need to create token with our test keys for this to work
        Map<String, Object> claims = Map.of(
                TokenClaims.USER_ID.getValue(), userId,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );
        Token token = tokenService.generateToken(claims);

        // When / Then - should not throw
        tokenService.verifyAndValidate(token.getAccessToken());
    }

    @Test
    @DisplayName("Should throw on expired token")
    void shouldThrowOnExpiredToken() {
        // Given
        String expiredToken = TokenFixtures.createExpiredToken();

        // When / Then
        assertThatThrownBy(() -> tokenService.verifyAndValidate(expiredToken))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token has expired");
    }

    @Test
    @DisplayName("Should throw on invalid signature")
    void shouldThrowOnInvalidSignature() {
        // Given
        String invalidSignatureToken = TokenFixtures.createInvalidSignatureToken();

        // When / Then
        assertThatThrownBy(() -> tokenService.verifyAndValidate(invalidSignatureToken))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid JWT token");
    }

    @Test
    @DisplayName("Should throw on malformed token")
    void shouldThrowOnMalformedToken() {
        // Given
        String malformedToken = TokenFixtures.createMalformedToken();

        // When / Then
        assertThatThrownBy(() -> tokenService.verifyAndValidate(malformedToken))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("Should generate token with claims")
    void shouldGenerateTokenWithClaims() {
        // Given
        String userId = UUID.randomUUID().toString();
        Map<String, Object> claims = Map.of(
                TokenClaims.USER_ID.getValue(), userId,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );

        // When
        Token token = tokenService.generateToken(claims);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.getAccessToken()).isNotBlank();
        assertThat(token.getRefreshToken()).isNotBlank();
        assertThat(token.getAccessTokenExpiresAt()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should get authentication from token")
    void shouldGetAuthenticationFromToken() {
        // Given
        String userId = UUID.randomUUID().toString();
        Map<String, Object> claims = Map.of(
                TokenClaims.USER_ID.getValue(), userId,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );
        Token token = tokenService.generateToken(claims);

        // When
        UsernamePasswordAuthenticationToken authentication = tokenService.getAuthentication(token.getAccessToken());

        // Then
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should get payload from token")
    void shouldGetPayloadFromToken() {
        // Given
        String userId = UUID.randomUUID().toString();
        Map<String, Object> claims = Map.of(
                TokenClaims.USER_ID.getValue(), userId,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );
        Token token = tokenService.generateToken(claims);

        // When
        Claims payload = tokenService.getPayload(token.getAccessToken());

        // Then
        assertThat(payload).isNotNull();
        assertThat(payload.get(TokenClaims.USER_ID.getValue())).isEqualTo(userId);
        assertThat(payload.get(TokenClaims.USER_TYPE.getValue())).isEqualTo(UserType.USER.name());
    }

    @Test
    @DisplayName("Should get ID from token")
    void shouldGetIdFromToken() {
        // Given
        String userId = UUID.randomUUID().toString();
        Map<String, Object> claims = Map.of(
                TokenClaims.USER_ID.getValue(), userId,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );
        Token token = tokenService.generateToken(claims);

        // When
        String tokenId = tokenService.getId(token.getAccessToken());

        // Then
        assertThat(tokenId).isNotBlank();
    }

    @Test
    @DisplayName("Should verify and validate multiple tokens")
    void shouldVerifyAndValidateMultipleTokens() {
        // Given
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        
        Map<String, Object> claims1 = Map.of(
                TokenClaims.USER_ID.getValue(), userId1,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );
        Map<String, Object> claims2 = Map.of(
                TokenClaims.USER_ID.getValue(), userId2,
                TokenClaims.USER_TYPE.getValue(), UserType.ADMIN.name()
        );
        
        Token token1 = tokenService.generateToken(claims1);
        Token token2 = tokenService.generateToken(claims2);
        
        Set<String> tokens = Set.of(token1.getAccessToken(), token2.getAccessToken());

        // When / Then - should not throw
        tokenService.verifyAndValidate(tokens);
    }

    @Test
    @DisplayName("Should generate token with refresh token")
    void shouldGenerateTokenWithRefreshToken() {
        // Given
        String userId = UUID.randomUUID().toString();
        Map<String, Object> claims = Map.of(
                TokenClaims.USER_ID.getValue(), userId,
                TokenClaims.USER_TYPE.getValue(), UserType.USER.name()
        );
        Token originalToken = tokenService.generateToken(claims);
        
        // Mock InvalidTokenService to not throw
        doNothing().when(invalidTokenService).checkForInvalidityOfToken(anyString());

        // When
        Token newToken = tokenService.generateToken(claims, originalToken.getRefreshToken());

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken.getAccessToken()).isNotBlank();
        assertThat(newToken.getRefreshToken()).isEqualTo(originalToken.getRefreshToken());
        assertThat(newToken.getAccessTokenExpiresAt()).isGreaterThan(0);
        
        verify(invalidTokenService).checkForInvalidityOfToken(anyString());
    }
}