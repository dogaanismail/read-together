package org.readtogether.acceptance.support;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Test data fixtures for acceptance tests.
 * Provides builders for creating test users, authentication payloads, etc.
 */
@UtilityClass
public class Fixtures {
    
    /**
     * User fixtures for creating test user data.
     */
    @UtilityClass
    public static class User {
        
        /**
         * Create a default user registration payload.
         */
        public static Map<String, Object> createDefaultRegisterRequest() {
            return createRegisterRequest(
                    generateUniqueEmail(),
                    "Password1!",
                    "John",
                    "Doe",
                    "user"
            );
        }
        
        /**
         * Create a custom user registration payload.
         */
        public static Map<String, Object> createRegisterRequest(
                String email,
                String password,
                String firstName,
                String lastName,
                String userType) {
            
            Map<String, Object> request = new HashMap<>();
            request.put("email", email);
            request.put("password", password);
            request.put("firstName", firstName);
            request.put("lastName", lastName);
            request.put("userType", userType);
            return request;
        }
        
        /**
         * Create a user registration request with invalid data.
         */
        public static Map<String, Object> createInvalidRegisterRequest() {
            return createRegisterRequest(
                    "invalid-email",  // Invalid email format
                    "weak",          // Weak password
                    "",              // Empty first name
                    "",              // Empty last name
                    "invalid"        // Invalid user type
            );
        }
        
        /**
         * Generate a unique email address for testing.
         */
        public static String generateUniqueEmail() {
            return "test.user." + UUID.randomUUID().toString().substring(0, 8) + "@test.local";
        }
    }
    
    /**
     * Authentication fixtures for login and token operations.
     */
    @UtilityClass
    public static class Auth {
        
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
         * Create a default login request.
         */
        public static Map<String, Object> createDefaultLoginRequest() {
            return createLoginRequest("test@example.com", "Password1!");
        }
        
        /**
         * Create a login request with invalid credentials.
         */
        public static Map<String, Object> createInvalidLoginRequest() {
            return createLoginRequest("nonexistent@test.local", "WrongPassword");
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
         * Create an invalid token refresh request.
         */
        public static Map<String, Object> createInvalidTokenRefreshRequest() {
            return createTokenRefreshRequest("invalid-refresh-token");
        }
    }
    
    /**
     * Profile fixtures for user profile operations.
     */
    @UtilityClass
    public static class Profile {
        
        /**
         * Create a profile update request.
         */
        public static Map<String, Object> createProfileUpdateRequest(
                String firstName,
                String lastName,
                String bio) {
            
            Map<String, Object> request = new HashMap<>();
            request.put("firstName", firstName);
            request.put("lastName", lastName);
            if (bio != null) {
                request.put("bio", bio);
            }
            return request;
        }
        
        /**
         * Create a default profile update request.
         */
        public static Map<String, Object> createDefaultProfileUpdateRequest() {
            return createProfileUpdateRequest(
                    "Updated First",
                    "Updated Last",
                    "Updated bio for testing"
            );
        }
    }
    
    /**
     * Common test data values.
     */
    @UtilityClass
    public static class Common {
        
        public static final String DEFAULT_PASSWORD = "Password1!";
        public static final String WEAK_PASSWORD = "weak";
        public static final String INVALID_EMAIL = "invalid-email";
        public static final String VALID_EMAIL_DOMAIN = "@test.local";
        
        /**
         * Generate a random string for testing.
         */
        public static String randomString(int length) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, Math.min(length, 32));
        }
        
        /**
         * Generate a unique identifier.
         */
        public static String uniqueId() {
            return UUID.randomUUID().toString();
        }
    }
}