package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import org.readtogether.user.model.User;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.mapper.UserEntityToUserMapper;
import org.readtogether.user.model.request.UpdateProfileRequest;
import org.readtogether.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.readtogether.security.common.enums.TokenClaims.USER_ID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityToUserMapper userEntityToUserMapper =
            UserEntityToUserMapper.initialize();

    public User getCurrentUser() {

        Optional<String> userId = getCurrentIdFromJwt();
        return userId
                .map(s -> getUser(UUID.fromString(s)))
                .orElse(null);
    }

    public User getUser(
            UUID userId) {

        Optional<UserEntity> optionalUserEntity = userRepository
                .findById(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException("User not found by id: " + userId);
        }

        UserEntity userEntity = optionalUserEntity.get();
        return userEntityToUserMapper.map(userEntity);
    }

    @Transactional
    public User updateCurrentUser(UpdateProfileRequest updateProfileRequest) {
        
        Optional<String> userId = getCurrentIdFromJwt();
        if (userId.isEmpty()) {
            throw new IllegalStateException("No authenticated user found");
        }

        UserEntity userEntity = findUserEntityById(UUID.fromString(userId.get()));
        
        // Update only non-null fields from the request
        if (updateProfileRequest.getFirstName() != null) {
            if (updateProfileRequest.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("First name cannot be empty");
            }
            userEntity.setFirstName(updateProfileRequest.getFirstName().trim());
        }
        
        if (updateProfileRequest.getLastName() != null) {
            if (updateProfileRequest.getLastName().trim().isEmpty()) {
                throw new IllegalArgumentException("Last name cannot be empty");
            }
            userEntity.setLastName(updateProfileRequest.getLastName().trim());
        }
        
        if (updateProfileRequest.getBio() != null) {
            userEntity.setBio(updateProfileRequest.getBio().trim());
        }
        
        if (updateProfileRequest.getProfilePictureUrl() != null) {
            userEntity.setProfilePictureUrl(updateProfileRequest.getProfilePictureUrl().trim());
        }
        
        if (updateProfileRequest.getUsername() != null) {
            if (updateProfileRequest.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            userEntity.setUsername(updateProfileRequest.getUsername().trim());
        }

        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userEntityToUserMapper.map(savedUserEntity);
    }

    public UserEntity findUserEntityById(
            UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + userId));
    }

    private Optional<String> getCurrentIdFromJwt() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> !"anonymousUser".equals(principal))
                .map(Jwt.class::cast)
                .map(jwt -> jwt.getClaim(USER_ID.getValue()))
                .map(Object::toString);
    }

}
