package org.readtogether.security.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.security.common.enums.TokenClaims;
import org.readtogether.security.fixtures.TokenRequestFixtures;
import org.readtogether.security.model.Token;
import org.readtogether.security.model.request.TokenRefreshRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.exception.UserStatusNotValidException;
import org.readtogether.user.repository.UserRepository;
import org.readtogether.user.common.enums.UserStatus;
import org.readtogether.user.common.enums.UserType;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Tests")
class RefreshTokenServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(userRepository, tokenService);
    }

    @Test
    @DisplayName("Should refresh with valid refresh token")
    void shouldRefreshWithValidRefreshToken() {
        // Given
        String userId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(refreshToken);

        UserEntity userEntity = UserEntity.builder()
                .id(UUID.fromString(userId))
                .email("test@example.com")
                .userStatus(UserStatus.ACTIVE)
                .userType(UserType.USER)
                .build();

        Claims claims = mock(Claims.class);
        when(claims.get(TokenClaims.USER_ID.getValue())).thenReturn(userId);

        Token expectedToken = Token.builder()
                .accessToken("new-access-token")
                .refreshToken(refreshToken)
                .accessTokenExpiresAt(System.currentTimeMillis() + 3600000)
                .build();

        doNothing().when(tokenService).verifyAndValidate(refreshToken);
        when(tokenService.getPayload(refreshToken)).thenReturn(claims);
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(userEntity));
        when(tokenService.generateToken(any(Map.class), eq(refreshToken))).thenReturn(expectedToken);

        // When
        Token result = refreshTokenService.refreshToken(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);

        verify(tokenService).verifyAndValidate(refreshToken);
        verify(tokenService).getPayload(refreshToken);
        verify(userRepository).findById(UUID.fromString(userId));
        verify(tokenService).generateToken(any(Map.class), eq(refreshToken));
    }

    @Test
    @DisplayName("Should throw on expired refresh token")
    void shouldThrowOnExpiredRefreshToken() {
        // Given
        String expiredRefreshToken = "expired-refresh-token";
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(expiredRefreshToken);

        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Token has expired"))
                .when(tokenService).verifyAndValidate(expiredRefreshToken);

        // When / Then
        assertThatThrownBy(() -> refreshTokenService.refreshToken(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token has expired");

        verify(tokenService).verifyAndValidate(expiredRefreshToken);
        verifyNoMoreInteractions(tokenService, userRepository);
    }

    @Test
    @DisplayName("Should throw on wrong token type")
    void shouldThrowOnWrongTokenType() {
        // Given
        String wrongTypeToken = "wrong-type-token";
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(wrongTypeToken);

        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid JWT token"))
                .when(tokenService).verifyAndValidate(wrongTypeToken);

        // When / Then
        assertThatThrownBy(() -> refreshTokenService.refreshToken(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid JWT token");

        verify(tokenService).verifyAndValidate(wrongTypeToken);
        verifyNoMoreInteractions(tokenService, userRepository);
    }

    @Test
    @DisplayName("Should throw when user not found")
    void shouldThrowWhenUserNotFound() {
        // Given
        String userId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(refreshToken);

        Claims claims = mock(Claims.class);
        when(claims.get(TokenClaims.USER_ID.getValue())).thenReturn(userId);

        doNothing().when(tokenService).verifyAndValidate(refreshToken);
        when(tokenService.getPayload(refreshToken)).thenReturn(claims);
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> refreshTokenService.refreshToken(request))
                .isInstanceOf(UserNotFoundException.class);

        verify(tokenService).verifyAndValidate(refreshToken);
        verify(tokenService).getPayload(refreshToken);
        verify(userRepository).findById(UUID.fromString(userId));
        verifyNoMoreInteractions(tokenService);
    }

    @Test
    @DisplayName("Should throw when user status is not active")
    void shouldThrowWhenUserStatusIsNotActive() {
        // Given
        String userId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(refreshToken);

        UserEntity inactiveUser = UserEntity.builder()
                .id(UUID.fromString(userId))
                .email("test@example.com")
                .userStatus(UserStatus.PASSIVE)
                .userType(UserType.USER)
                .build();

        Claims claims = mock(Claims.class);
        when(claims.get(TokenClaims.USER_ID.getValue())).thenReturn(userId);

        doNothing().when(tokenService).verifyAndValidate(refreshToken);
        when(tokenService.getPayload(refreshToken)).thenReturn(claims);
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(inactiveUser));

        // When / Then
        assertThatThrownBy(() -> refreshTokenService.refreshToken(request))
                .isInstanceOf(UserStatusNotValidException.class)
                .hasMessageContaining("UserStatus = PASSIVE");

        verify(tokenService).verifyAndValidate(refreshToken);
        verify(tokenService).getPayload(refreshToken);
        verify(userRepository).findById(UUID.fromString(userId));
        verifyNoMoreInteractions(tokenService);
    }

    @Test
    @DisplayName("Should handle passive user status")
    void shouldHandlePassiveUserStatus() {
        // Given
        String userId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(refreshToken);

        UserEntity passiveUser = UserEntity.builder()
                .id(UUID.fromString(userId))
                .email("test@example.com")
                .userStatus(UserStatus.PASSIVE)
                .userType(UserType.USER)
                .build();

        Claims claims = mock(Claims.class);
        when(claims.get(TokenClaims.USER_ID.getValue())).thenReturn(userId);

        doNothing().when(tokenService).verifyAndValidate(refreshToken);
        when(tokenService.getPayload(refreshToken)).thenReturn(claims);
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(passiveUser));

        // When / Then
        assertThatThrownBy(() -> refreshTokenService.refreshToken(request))
                .isInstanceOf(UserStatusNotValidException.class)
                .hasMessageContaining("UserStatus = PASSIVE");

        verify(tokenService).verifyAndValidate(refreshToken);
        verify(tokenService).getPayload(refreshToken);
        verify(userRepository).findById(UUID.fromString(userId));
        verifyNoMoreInteractions(tokenService);
    }
}