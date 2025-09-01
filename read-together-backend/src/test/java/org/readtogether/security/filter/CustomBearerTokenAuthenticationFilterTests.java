package org.readtogether.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.security.fixtures.SecurityContextFixtures;
import org.readtogether.security.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag( "unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomBearerTokenAuthenticationFilter Tests")
class CustomBearerTokenAuthenticationFilterTests {

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CustomBearerTokenAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CustomBearerTokenAuthenticationFilter(tokenService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip when no authorization header")
    void shouldSkipWhenNoAuthorizationHeader() throws Exception {
        // Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip when non-bearer scheme")
    void shouldSkipWhenNonBearerScheme() throws Exception {
        // Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNzd29yZA==");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip when authorization header is empty")
    void shouldSkipWhenAuthorizationHeaderIsEmpty() throws Exception {
        // Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip when authorization header is just Bearer without token")
    void shouldSkipWhenAuthorizationHeaderIsJustBearer() throws Exception {
        // Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");
        
        // Since "Bearer" is technically a valid bearer token prefix, it will call getAuthentication
        // with an empty string token, which should trigger our exception handling
        when(tokenService.getAuthentication(""))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Empty token"));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService).getAuthentication(""); // Empty token after removing "Bearer "
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should authenticate on valid token")
    void shouldAuthenticateOnValidToken() throws Exception {
        // Given
        String token = "valid-jwt-token";
        String authHeader = "Bearer " + token;
        UsernamePasswordAuthenticationToken authentication = 
                (UsernamePasswordAuthenticationToken) SecurityContextFixtures.buildUserAuthentication("user-id");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(tokenService.getAuthentication(token)).thenReturn(authentication);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService).getAuthentication(token);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("Should handle exception from token service gracefully")
    void shouldHandleExceptionFromTokenServiceGracefully() throws Exception {
        // Given
        String token = "invalid-jwt-token";
        String authHeader = "Bearer " + token;

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(tokenService.getAuthentication(token))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid token"));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService).getAuthentication(token);
        verify(filterChain).doFilter(request, response);
        // Authentication should not be set due to exception
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should process multiple bearer tokens appropriately")
    void shouldProcessMultipleBearerTokensAppropriately() throws Exception {
        // Given
        String token1 = "valid-jwt-token-1";
        String authHeader1 = "Bearer " + token1;
        UsernamePasswordAuthenticationToken authentication1 = 
                (UsernamePasswordAuthenticationToken) SecurityContextFixtures.buildUserAuthentication("user-id-1");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader1);
        when(tokenService.getAuthentication(token1)).thenReturn(authentication1);

        // When - First request
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService).getAuthentication(token1);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication1);

        // Clear context for the next request
        SecurityContextHolder.clearContext();

        // Given - Second request
        String token2 = "valid-jwt-token-2";
        String authHeader2 = "Bearer " + token2;
        UsernamePasswordAuthenticationToken authentication2 = 
                (UsernamePasswordAuthenticationToken) SecurityContextFixtures.buildAdminAuthentication("admin-id");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader2);
        when(tokenService.getAuthentication(token2)).thenReturn(authentication2);

        // When - Second request
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService).getAuthentication(token2);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication2);
    }

    @Test
    @DisplayName("Should handle malformed bearer token")
    void shouldHandleMalformedBearerToken() throws Exception {
        // Given
        String malformedToken = "malformed.token";
        String authHeader = "Bearer " + malformedToken;

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(tokenService.getAuthentication(malformedToken))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Malformed token"));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenService).getAuthentication(malformedToken);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}