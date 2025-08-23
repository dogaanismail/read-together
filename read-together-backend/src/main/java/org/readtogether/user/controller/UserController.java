package org.readtogether.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.security.model.Token;
import org.readtogether.user.model.User;
import org.readtogether.security.model.request.TokenInvalidateRequest;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.security.model.response.TokenResponse;
import org.readtogether.user.mapper.TokenToTokenResponseMapper;
import org.readtogether.user.service.LogoutService;
import org.readtogether.user.service.RegisterService;
import org.readtogether.user.service.UserLoginService;
import org.readtogether.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
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

}
