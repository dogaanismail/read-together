package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import org.readtogether.common.model.auth.User;
import org.readtogether.common.model.auth.dto.request.UpdateProfileRequest;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.exception.UserNotFoundException;
import org.readtogether.user.model.user.mapper.UserEntityToUserMapper;
import org.readtogether.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.readtogether.common.enums.TokenClaims.USER_ID;

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

    public User getUser(UUID userId) {

        Optional<UserEntity> optionalUserEntity = userRepository
                .findById(userId);

        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException("User not found by id: " + userId);
        }

        UserEntity userEntity = optionalUserEntity.get();
        return userEntityToUserMapper.map(userEntity);
    }

    public UserEntity findUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + userId));
    }

    public User updateProfile(UpdateProfileRequest updateRequest) {
        Optional<String> currentUserId = getCurrentIdFromJwt();

        if (currentUserId.isEmpty()) {
            throw new IllegalStateException("User not authenticated");
        }

        UUID userId = UUID.fromString(currentUserId.get());
        UserEntity userEntity = findUserEntityById(userId);

        // Update fields if provided
        if (updateRequest.getFirstName() != null) {
            userEntity.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            userEntity.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getUsername() != null) {
            userEntity.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getBio() != null) {
            userEntity.setBio(updateRequest.getBio());
        }
        if (updateRequest.getProfilePictureUrl() != null) {
            userEntity.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
        }

        userEntity = userRepository.save(userEntity);
        return userEntityToUserMapper.map(userEntity);
    }

    public String uploadProfilePicture(MultipartFile file) {
        // For now, return a mock URL
        // In a real implementation, this would upload to AWS S3 or similar service
        String fileName = "profile_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // TODO: Implement actual file upload to cloud storage

        return "https://storage.readtogether.app/profiles/" + fileName;
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
