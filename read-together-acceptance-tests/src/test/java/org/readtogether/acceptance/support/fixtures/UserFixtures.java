package org.readtogether.acceptance.support.fixtures;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User-related test fixtures for acceptance tests.
 * Provides builders for creating user registration and profile data.
 */
@UtilityClass
public class UserFixtures {

    /**
     * Create a default user registration payload.
     */
    public static Map<String, Object> createDefaultRegisterRequest() {
        return createRegisterRequest(
                generateUniqueEmail(),
                "Password1!",
                "John",
                "Doe"
        );
    }

    /**
     * Create a custom user registration payload.
     */
    public static Map<String, Object> createRegisterRequest(
            String email,
            String password,
            String firstName,
            String lastName) {

        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        request.put("firstName", firstName);
        request.put("lastName", lastName);

        return request;
    }

    /**
     * Create profile update payload.
     */
    public static Map<String, Object> createProfileUpdateRequest(
            String firstName,
            String lastName,
            String bio) {

        Map<String, Object> request = new HashMap<>();
        request.put("firstName", firstName);
        request.put("lastName", lastName);
        request.put("bio", bio);

        return request;
    }

    /**
     * Generate a unique email address for testing.
     */
    public static String generateUniqueEmail() {
        return "test." + UUID.randomUUID().toString().substring(0, 8) + "@test.local";
    }

    /**
     * Create user with invalid email format.
     */
    public static Map<String, Object> createInvalidEmailRequest() {
        return createRegisterRequest(
                "invalid-email",
                "Password1!",
                "John",
                "Doe"
        );
    }

    /**
     * Create user with weak password.
     */
    public static Map<String, Object> createWeakPasswordRequest() {
        return createRegisterRequest(
                generateUniqueEmail(),
                "weak",
                "John",
                "Doe"
        );
    }

    /**
     * Create a user with missing required fields.
     */
    public static Map<String, Object> createIncompleteRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("email", generateUniqueEmail());
        return request;
    }
}