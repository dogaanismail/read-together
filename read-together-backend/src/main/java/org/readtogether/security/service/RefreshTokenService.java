package org.readtogether.security.service;

import lombok.RequiredArgsConstructor;
import org.readtogether.security.model.Token;
import org.readtogether.security.model.request.TokenRefreshRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.exception.UserStatusNotValidException;
import org.readtogether.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.readtogether.security.common.enums.TokenClaims.USER_ID;
import static org.readtogether.user.common.enums.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public Token refreshToken(TokenRefreshRequest tokenRefreshRequest) {

        tokenService.verifyAndValidate(tokenRefreshRequest.getRefreshToken());

        String userId = tokenService
                .getPayload(tokenRefreshRequest.getRefreshToken())
                .get(USER_ID.getValue())
                .toString();

        UserEntity userEntityFromDB = userRepository
                .findById(UUID.fromString(userId))
                .orElseThrow(UserNotFoundException::new);

        this.validateUserStatus(userEntityFromDB);

        return tokenService.generateToken(
                userEntityFromDB.getClaims(),
                tokenRefreshRequest.getRefreshToken()
        );

    }

    private void validateUserStatus(UserEntity userEntity) {

        if (!(ACTIVE.equals(userEntity.getUserStatus()))) {
            throw new UserStatusNotValidException("UserStatus = " + userEntity.getUserStatus());
        }
    }

}
