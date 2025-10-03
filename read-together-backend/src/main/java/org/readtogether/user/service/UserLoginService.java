package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import org.readtogether.security.model.Token;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.security.service.TokenService;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.PasswordNotValidException;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public Token login(
            LoginRequest loginRequest) {

        Optional<UserEntity> optionalUserEntity = userRepository
                .findByEmail(loginRequest.getEmail());

        if (optionalUserEntity.isEmpty()) {

            throw new UserNotFoundException("Can't find with given email: "
                    + loginRequest.getEmail());
        }

        UserEntity userEntity = optionalUserEntity.get();

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                userEntity.getPassword())) {

            throw new PasswordNotValidException();
        }

        return tokenService.generateToken(userEntity.getClaims());
    }

}
