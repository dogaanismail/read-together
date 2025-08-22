package org.readtogether.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.model.CustomResponse;
import org.readtogether.common.model.Token;
import org.readtogether.common.model.auth.User;
import org.readtogether.common.model.auth.dto.request.LoginRequest;
import org.readtogether.common.model.auth.dto.request.RegisterRequest;
import org.readtogether.common.model.auth.dto.request.TokenInvalidateRequest;
import org.readtogether.common.model.auth.dto.request.ForgotPasswordRequest;
import org.readtogether.common.model.auth.dto.request.UpdateProfileRequest;
import org.readtogether.common.model.auth.dto.response.TokenResponse;
import org.readtogether.common.model.auth.dto.response.ProfilePictureResponse;
import org.readtogether.user.model.user.mapper.TokenToTokenResponseMapper;
import org.readtogether.user.service.LogoutService;
import org.readtogether.user.service.RegisterService;
import org.readtogether.user.service.UserLoginService;
import org.readtogether.user.service.UserService;
import org.readtogether.notification.service.NotificationProviderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserLoginService userLoginService;
    private final UserService userService;
    private final LogoutService logoutService;
    private final RegisterService registerService;
    private final NotificationProviderService notificationProviderService;
    private final TokenToTokenResponseMapper tokenToTokenResponseMapper = TokenToTokenResponseMapper
            .initialize();

    @PostMapping("/register")
    public CustomResponse<Void> register(
            @RequestBody @Validated RegisterRequest registerRequest) {

        log.info("Received a request to register a new user");
        registerService.registerUser(registerRequest);
        return CustomResponse.SUCCESS;
    }

    @PostMapping("/login")
    public CustomResponse<TokenResponse> login(
            @RequestBody @Valid LoginRequest loginRequest) {

        log.info("Received a request to login a user");
        Token token = userLoginService.login(loginRequest);

        TokenResponse tokenResponse = tokenToTokenResponseMapper.map(token);
        log.info("User logged in successfully");

        return CustomResponse.successOf(tokenResponse);
    }

    @PostMapping("/logout")
    public CustomResponse<Void> logout(
            @RequestBody @Valid TokenInvalidateRequest tokenInvalidateRequest) {

        log.info("Received a request to logout a user");
        logoutService.logout(tokenInvalidateRequest);
        return CustomResponse.SUCCESS;
    }

    @PostMapping("/forgot-password")
    public CustomResponse<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {

        log.info("Received a request to send forgot password email");
        
        try {
            notificationProviderService.sendForgotPasswordEmail(forgotPasswordRequest.getEmail());
            log.info("Forgot password email sent successfully");
            return CustomResponse.SUCCESS;
        } catch (Exception e) {
            log.error("Failed to send forgot password email", e);
            return CustomResponse.SUCCESS;
        }
    }

    @GetMapping("/user")
    public CustomResponse<User> getUser(
            @RequestParam(name = "userId") UUID userId) {

        log.info("Received a request to get user by id, {}", userId);
        User user = userService.getUser(userId);
        return CustomResponse.successOf(user);
    }

    @GetMapping("/current-user")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<User> getCurrentUser() {

        log.info("Received a request to get current user");
        User user = userService.getCurrentUser();
        return CustomResponse.successOf(user);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<User> updateProfile(
            @RequestBody @Valid UpdateProfileRequest updateProfileRequest) {

        log.info("Received a request to update user profile");
        User updatedUser = userService.updateProfile(updateProfileRequest);
        return CustomResponse.successOf(updatedUser);
    }

    @PostMapping("/profile/picture")
    @PreAuthorize("hasAuthority('USER')")
    public CustomResponse<ProfilePictureResponse> uploadProfilePicture(
            @RequestParam("profilePicture") MultipartFile file) {

        log.info("Received a request to upload profile picture");
        
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new IllegalArgumentException("File size cannot exceed 5MB");
        }
        
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        String profilePictureUrl = userService.uploadProfilePicture(file);
        ProfilePictureResponse response = new ProfilePictureResponse(profilePictureUrl);
        return CustomResponse.successOf(response);
    }

}
