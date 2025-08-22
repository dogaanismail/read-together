package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserAlreadyExistException;
import org.readtogether.user.factory.UserEntityFactory;
import org.readtogether.user.mapper.UserEntityToUserMapper;
import org.readtogether.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityToUserMapper userEntityToUserMapper =
            UserEntityToUserMapper.initialize();

    public void registerUser(RegisterRequest registerRequest) {

        isEmailExist(registerRequest.getEmail());

        UserEntity userEntityToBeSave = UserEntityFactory
                .getUserEntityByRegisterRequest(registerRequest);
        userEntityToBeSave.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        UserEntity savedUserEntity = userRepository.save(userEntityToBeSave);
        userEntityToUserMapper.map(savedUserEntity);
    }

    private void isEmailExist(String email) {

        if (userRepository.existsUserEntityByEmail(email)) {
            throw new UserAlreadyExistException("The email is already used for another user : " + email);
        }

    }

}


