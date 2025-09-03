package org.readtogether.acceptance.support.fixtures;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication-related test fixtures for acceptance tests.
 * Provides builders for creating login and token refresh requests.
 */
@UtilityClass
public class AuthFixtures {
    
    public static final String DEFAULT_PASSWORD = "Password1!";
    
    /**
     * Create a login request payload.
     */
    public static Map<String, Object> createLoginRequest(String email, String password) {
        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        return request;
    }
    
    /**
     * Create a token refresh request payload.
     */
    public static Map<String, Object> createTokenRefreshRequest(String refreshToken) {
        Map<String, Object> request = new HashMap<>();
        request.put("refreshToken", refreshToken);
        return request;
    }
    
    /**
     * Create a login request with invalid credentials.
     */
    public static Map<String, Object> createInvalidLoginRequest() {
        return createLoginRequest("nonexistent@test.local", "WrongPassword");
    }
    
    /**
     * Create login request with missing email.
     */
    public static Map<String, Object> createMissingEmailLoginRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("password", DEFAULT_PASSWORD);
        return request;
    }
    
    /**
     * Create login request with missing password.
     */
    public static Map<String, Object> createMissingPasswordLoginRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "test@test.local");
        return request;
    }
    
    /**
     * Create a login request with empty fields.
     */
    public static Map<String, Object> createEmptyFieldsLoginRequest() {
        return createLoginRequest("", "");
    }
}