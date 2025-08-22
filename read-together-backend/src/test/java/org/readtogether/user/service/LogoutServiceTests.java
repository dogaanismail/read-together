package org.readtogether.user.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.security.exception.TokenAlreadyInvalidatedException;
import org.readtogether.security.model.request.TokenInvalidateRequest;
import org.readtogether.security.service.InvalidTokenService;
import org.readtogether.security.service.TokenService;
import org.readtogether.user.fixtures.RequestFixtures;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutService Tests")
class LogoutServiceTests {

    @Mock
    private TokenService tokenService;

    @Mock
    private InvalidTokenService invalidTokenService;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    @DisplayName("Should verify, check invalidity and invalidate both tokens")
    void shouldVerifyCheckAndInvalidateBothTokens() {
        // Given
        String accessToken = "access.jwt";
        String refreshToken = "refresh.jwt";
        String accessId = "acc-123";
        String refreshId = "ref-456";

        Claims accessClaims = mock(Claims.class);
        Claims refreshClaims = mock(Claims.class);

        when(tokenService.getPayload(accessToken)).thenReturn(accessClaims);
        when(tokenService.getPayload(refreshToken)).thenReturn(refreshClaims);
        when(accessClaims.getId()).thenReturn(accessId);
        when(refreshClaims.getId()).thenReturn(refreshId);

        TokenInvalidateRequest request = RequestFixtures.
                createTokenInvalidateRequest(accessToken, refreshToken);

        // When
        logoutService.logout(request);

        // Then
        verify(tokenService).verifyAndValidate(eq(Set.of(accessToken, refreshToken)));

        verify(tokenService).getPayload(accessToken);
        verify(tokenService).getPayload(refreshToken);

        verify(invalidTokenService).checkForInvalidityOfToken(accessId);
        verify(invalidTokenService).checkForInvalidityOfToken(refreshId);
        verify(invalidTokenService).invalidateTokens(eq(Set.of(accessId, refreshId)));

        verifyNoMoreInteractions(tokenService, invalidTokenService);
    }

    @Test
    @DisplayName("Should stop and propagate when access token already invalidated")
    void shouldPropagateWhenAccessTokenAlreadyInvalidated() {
        // Given
        String accessToken = "access.jwt";
        String refreshToken = "refresh.jwt";
        String accessId = "acc-123";

        Claims accessClaims = mock(Claims.class);
        when(tokenService.getPayload(accessToken)).thenReturn(accessClaims);
        when(accessClaims.getId()).thenReturn(accessId);

        doThrow(new TokenAlreadyInvalidatedException(accessId))
                .when(invalidTokenService).checkForInvalidityOfToken(accessId);

        TokenInvalidateRequest request = RequestFixtures.
                createTokenInvalidateRequest(accessToken, refreshToken);

        // When & Then
        assertThatThrownBy(() -> logoutService.logout(request))
                .isInstanceOf(TokenAlreadyInvalidatedException.class)
                .hasMessageContaining(accessId);

        verify(tokenService).verifyAndValidate(eq(Set.of(accessToken, refreshToken)));
        verify(tokenService).getPayload(accessToken);
        verify(invalidTokenService).checkForInvalidityOfToken(accessId);
        verify(invalidTokenService, never()).invalidateTokens(any());
        verify(tokenService, never()).getPayload(refreshToken);
    }

    @Test
    @DisplayName("Should stop and propagate when refresh token already invalidated")
    void shouldPropagateWhenRefreshTokenAlreadyInvalidated() {
        // Given
        String accessToken = "access.jwt";
        String refreshToken = "refresh.jwt";
        String accessId = "acc-123";
        String refreshId = "ref-456";

        Claims accessClaims = mock(Claims.class);
        Claims refreshClaims = mock(Claims.class);

        when(tokenService.getPayload(accessToken)).thenReturn(accessClaims);
        when(tokenService.getPayload(refreshToken)).thenReturn(refreshClaims);
        when(accessClaims.getId()).thenReturn(accessId);
        when(refreshClaims.getId()).thenReturn(refreshId);

        doNothing().when(invalidTokenService).checkForInvalidityOfToken(accessId);
        doThrow(new TokenAlreadyInvalidatedException(refreshId))
                .when(invalidTokenService).checkForInvalidityOfToken(refreshId);

        TokenInvalidateRequest request = RequestFixtures.
                createTokenInvalidateRequest(accessToken, refreshToken);

        // When & Then
        assertThatThrownBy(() -> logoutService.logout(request))
                .isInstanceOf(TokenAlreadyInvalidatedException.class)
                .hasMessageContaining(refreshId);

        verify(tokenService).verifyAndValidate(eq(Set.of(accessToken, refreshToken)));
        verify(tokenService).getPayload(accessToken);
        verify(invalidTokenService).checkForInvalidityOfToken(accessId);
        verify(tokenService).getPayload(refreshToken);
        verify(invalidTokenService).checkForInvalidityOfToken(refreshId);
        verify(invalidTokenService, never()).invalidateTokens(any());
    }

    @Test
    @DisplayName("Should propagate validation error from TokenService and not proceed")
    void shouldPropagateValidationErrorFromTokenService() {
        // Given
        String accessToken = "access.jwt";
        String refreshToken = "refresh.jwt";

        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "invalid"))
                .when(tokenService).verifyAndValidate(anySet());

        TokenInvalidateRequest request = RequestFixtures.
                createTokenInvalidateRequest(accessToken, refreshToken);

        // When & Then
        assertThatThrownBy(() -> logoutService.logout(request))
                .isInstanceOf(ResponseStatusException.class);

        verify(tokenService).verifyAndValidate(eq(Set.of(accessToken, refreshToken)));
        verify(tokenService, never()).getPayload(anyString());
        verifyNoInteractions(invalidTokenService);
    }

    @Test
    @DisplayName("Should propagate when getting payload fails and not invalidate")
    void shouldPropagateWhenGetPayloadFails() {
        // Given
        String accessToken = "access.jwt";
        String refreshToken = "refresh.jwt";

        doThrow(new RuntimeException("payload error"))
                .when(tokenService).getPayload(accessToken);

        TokenInvalidateRequest request = RequestFixtures.
                createTokenInvalidateRequest(accessToken, refreshToken);

        // When & Then
        assertThatThrownBy(() -> logoutService.logout(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("payload error");

        verify(tokenService).verifyAndValidate(eq(Set.of(accessToken, refreshToken)));
        verify(tokenService).getPayload(accessToken);
        verifyNoInteractions(invalidTokenService);
        verify(tokenService, never()).getPayload(refreshToken);
    }
}
