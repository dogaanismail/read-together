package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.model.request.RegisterRequest;

@UtilityClass
public class RequestFixtures {

    public static RegisterRequest createDefaultRegisterRequest() {

        return RegisterRequest.builder()
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .role("user")
                .build();
    }

    public static RegisterRequest createAdminRegisterRequest() {
        return RegisterRequest.builder()
                .email("newadmin@example.com")
                .firstName("New")
                .lastName("Admin")
                .role("admin")
                .build();
    }

    public static RegisterRequest createRegisterRequest(
            String email,
            String firstName,
            String lastName,
            String role) {

        return RegisterRequest.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .build();
    }
}
