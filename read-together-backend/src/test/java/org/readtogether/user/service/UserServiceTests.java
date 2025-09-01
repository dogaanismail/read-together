package org.readtogether.user.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.model.User;
import org.readtogether.user.repository.UserRepository;
import org.readtogether.user.fixtures.UserEntityFixtures;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.readtogether.security.common.enums.TokenClaims.USER_ID;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTests {

    private static final UUID TEST_USER_ID = UserEntityFixtures.DEFAULT_USER_ID;
    private static final String TEST_USER_ID_STRING = TEST_USER_ID.toString();

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should get current user successfully when JWT contains valid user ID")
    void shouldGetCurrentUserSuccessfully() {
        // Given
        UserEntity testUserEntity = UserEntityFixtures.createDefaultUserEntity();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim(USER_ID.getValue())).thenReturn(TEST_USER_ID_STRING);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserEntity));

        // When
        User result = userService.getCurrentUser();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_USER_ID.toString());
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");

        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should return null when no authentication context exists")
    void shouldReturnNullWhenNoAuthenticationContext() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        User result = userService.getCurrentUser();

        // Then
        assertThat(result).isNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return null when principal is anonymous user")
    void shouldReturnNullWhenPrincipalIsAnonymousUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // When
        User result = userService.getCurrentUser();

        // Then
        assertThat(result).isNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return null when JWT does not contain user ID claim")
    void shouldReturnNullWhenJwtMissingUserIdClaim() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim(USER_ID.getValue())).thenReturn(null);

        // When
        User result = userService.getCurrentUser();

        // Then
        assertThat(result).isNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        UserEntity testUserEntity = UserEntityFixtures.createDefaultUserEntity();
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserEntity));

        // When
        User result = userService.getUser(TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_USER_ID.toString());
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by ID")
    void shouldThrowUserNotFoundExceptionWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUser(TEST_USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + TEST_USER_ID);

        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should find user entity by ID successfully")
    void shouldFindUserEntityByIdSuccessfully() {
        // Given
        UserEntity testUserEntity = UserEntityFixtures.createDefaultUserEntity();
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUserEntity));

        // When
        UserEntity result = userService.findUserEntityById(TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user entity not found by ID")
    void shouldThrowUserNotFoundExceptionWhenUserEntityNotFoundById() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findUserEntityById(TEST_USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + TEST_USER_ID);

        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should handle different user IDs correctly")
    void shouldHandleDifferentUserIdsCorrectly() {
        // Given
        UserEntity differentUserEntity = UserEntityFixtures.createSecondaryUserEntity();
        UUID differentUserId = UserEntityFixtures.SECONDARY_USER_ID;

        when(userRepository.findById(differentUserId)).thenReturn(Optional.of(differentUserEntity));

        // When
        User result = userService.getUser(differentUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(differentUserId.toString());
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        assertThat(result.getFirstName()).isEqualTo("Jane");

        verify(userRepository).findById(differentUserId);
    }

    @Test
    @DisplayName("Should handle JWT with invalid user ID format")
    void shouldHandleJwtWithInvalidUserIdFormat() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim(USER_ID.getValue())).thenReturn("invalid-uuid-format");

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return null when current user is found but user entity does not exist")
    void shouldReturnNullWhenCurrentUserFoundButUserEntityNotExists() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim(USER_ID.getValue())).thenReturn(TEST_USER_ID_STRING);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + TEST_USER_ID);

        verify(userRepository).findById(TEST_USER_ID);
    }
}
