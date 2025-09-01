package org.readtogether.common.utils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.readtogether.security.common.enums.TokenClaims.USER_EMAIL;
import static org.readtogether.security.common.enums.TokenClaims.USER_ID;
import static org.readtogether.user.common.enums.UserType.ANONYMOUS;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityUtils Tests")
class SecurityUtilsTest {

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Mock
    private SecurityContext securityContext;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {

        securityContextHolderMock.close();
    }

    @Test
    @DisplayName("Should return user ID when authentication contains valid JWT")
    void shouldReturnUserIdWhenAuthenticationContainsValidJwt() {

        UUID expectedUserId = UUID.randomUUID();
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString(USER_ID.getValue())).thenReturn(expectedUserId.toString());

        UUID actualUserId = SecurityUtils.getCurrentUserId(authentication);

        assertThat(actualUserId).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("Should throw exception when JWT claim contains invalid UUID format")
    void shouldThrowExceptionWhenJwtClaimContainsInvalidUuidFormat() {

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString(USER_ID.getValue())).thenReturn("invalid-uuid");

        assertThatThrownBy(() -> SecurityUtils.getCurrentUserId(authentication))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception when JWT claim contains null user ID")
    void shouldThrowExceptionWhenJwtClaimContainsNullUserId() {

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString(USER_ID.getValue())).thenReturn(null);

        assertThatThrownBy(() -> SecurityUtils.getCurrentUserId(authentication))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should return user email when security context contains authenticated user")
    void shouldReturnUserEmailWhenSecurityContextContainsAuthenticatedUser() {

        String expectedEmail = "user@example.com";

        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim(USER_EMAIL.getValue())).thenReturn(expectedEmail);

        String actualEmail = SecurityUtils.getCurrentUserEmail();

        assertThat(actualEmail).isEqualTo(expectedEmail);
    }

    @Test
    @DisplayName("Should return ANONYMOUS when security context has no authentication")
    void shouldReturnAnonymousWhenSecurityContextHasNoAuthentication() {

        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        String actualEmail = SecurityUtils.getCurrentUserEmail();

        assertThat(actualEmail).isEqualTo(ANONYMOUS.name());
    }

    @Test
    @DisplayName("Should return ANONYMOUS when principal is ANONYMOUS user")
    void shouldReturnAnonymousWhenPrincipalIsAnonymousUser() {

        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ANONYMOUS);

        String actualEmail = SecurityUtils.getCurrentUserEmail();

        assertThat(actualEmail).isEqualTo(ANONYMOUS.name());
    }

}
