package org.readtogether.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.security.model.Token;
import org.readtogether.security.service.TokenService;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.PasswordNotValidException;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.fixtures.TokenFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.repository.UserRepository;
import org.readtogether.user.fixtures.UserEntityFixtures;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserLoginService Tests")
class UserLoginServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserLoginService userLoginService;

    @Test
    @DisplayName("Should login successfully and return token")
    void shouldLoginSuccessfully() {
        // Given
        UserEntity userEntity = UserEntityFixtures.createDefaultUserEntity();
        userEntity.setPassword("encoded-password");

        LoginRequest loginRequest = RequestFixtures.createLoginRequest();

        Token expectedToken = TokenFixtures.createToken();

        when(userRepository.findUserEntityByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword()))
                .thenReturn(true);

        when(tokenService.generateToken(any())).thenReturn(expectedToken);

        // When
        Token result = userLoginService.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getAccessTokenExpiresAt()).isEqualTo(123456789L);
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");

        verify(userRepository).findUserEntityByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), userEntity.getPassword());
        verify(tokenService).generateToken(eq(userEntity.getClaims()));
        verifyNoMoreInteractions(userRepository, passwordEncoder, tokenService);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when email not found")
    void shouldThrowWhenUserNotFoundByEmail() {
        // Given
        LoginRequest loginRequest = RequestFixtures.createLoginRequest();

        when(userRepository.findUserEntityByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userLoginService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Can't find with given email: " + loginRequest.getEmail());

        verify(userRepository).findUserEntityByEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, tokenService);
    }

    @Test
    @DisplayName("Should throw PasswordNotValidException when password does not match")
    void shouldThrowWhenPasswordNotValid() {
        // Given
        UserEntity userEntity = UserEntityFixtures.createDefaultUserEntity();
        userEntity.setPassword("encoded-password");

        LoginRequest loginRequest = RequestFixtures.createLoginRequest();

        when(userRepository.findUserEntityByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userLoginService.login(loginRequest))
                .isInstanceOf(PasswordNotValidException.class);

        verify(userRepository).findUserEntityByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), userEntity.getPassword());
        verifyNoInteractions(tokenService);
    }
}

