package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.security.model.request.TokenInvalidateRequest;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;

@UtilityClass
public class RequestFixtures {

    public static RegisterRequest createDefaultRegisterRequest() {

        return RegisterRequest.builder()
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .build();
    }

    public static RegisterRequest createAdminRegisterRequest() {

        return RegisterRequest.builder()
                .email("newadmin@example.com")
                .firstName("New")
                .lastName("Admin")
                .build();
    }

    public static RegisterRequest createRegisterRequest(
            String email,
            String firstName,
            String lastName) {

        return RegisterRequest.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static RegisterRequest createRegisterRequest(
            String email,
            String password,
            String firstName,
            String lastName) {

        return RegisterRequest.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static LoginRequest createLoginRequest() {

        return LoginRequest.builder()
                .email("test@example.com")
                .password("plain-password")
                .build();
    }

    public static LoginRequest createLoginRequest(
            String email,
            String password) {

        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static TokenInvalidateRequest createTokenInvalidateRequest(
            String accessToken,
            String refreshToken) {

        return TokenInvalidateRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
