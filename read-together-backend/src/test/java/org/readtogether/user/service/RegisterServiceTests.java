package org.readtogether.user.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserAlreadyExistException;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.mapper.UserEntityToUserMapper;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterService Tests")
class RegisterServiceTests {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegisterService registerService;

    private MockedStatic<UserEntityToUserMapper> mapperStaticMock;
    private UserEntityToUserMapper mapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        mapper = mock(UserEntityToUserMapper.class);

        mapperStaticMock = Mockito.mockStatic(UserEntityToUserMapper.class);
        mapperStaticMock.when(UserEntityToUserMapper::initialize).thenReturn(mapper);

        registerService = new RegisterService(userRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        if (mapperStaticMock != null) {
            mapperStaticMock.close();
        }
    }

    @Test
    @DisplayName("Should register user successfully with default USER role")
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "new.user@example.com",
                "plain-pass",
                "New",
                "User"
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");

        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        registerService.registerUser(request);

        // Then
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode("plain-pass");

        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(entityCaptor.capture());

        UserEntity saved = entityCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo("new.user@example.com");
        assertThat(saved.getFirstName()).isEqualTo("New");
        assertThat(saved.getLastName()).isEqualTo("User");
        assertThat(saved.getPassword()).isEqualTo("encoded-pass");
        assertThat(saved.getUserType().name()).isEqualTo("USER");

        verify(mapper).map(saved);
        verifyNoMoreInteractions(userRepository, passwordEncoder, mapper);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistException when email already exists")
    void shouldThrowWhenEmailAlreadyExists() {
        // Given
        RegisterRequest request = RequestFixtures.createRegisterRequest(
                "example@example.com",
                "sample",
                "sample",
                "user"
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> registerService.registerUser(request))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("The email is already used for another user : " + request.getEmail());

        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, mapper);
    }
}
