package org.readtogether.acceptance.support;

import lombok.experimental.UtilityClass;
import org.readtogether.acceptance.support.fixtures.AuthFixtures;
import org.readtogether.acceptance.support.fixtures.UserFixtures;

import java.util.Map;

/**
 * Test data fixtures for acceptance tests.
 * Provides builders for creating test users, authentication payloads, etc.
 * 
 * Note: This class is kept for backward compatibility. 
 * New code should use individual fixture classes like UserFixtures, AuthFixtures instead.
 */
@UtilityClass
public class Fixtures {
    
    /**
     * User fixtures for creating test user data.
     */
    @UtilityClass
    public static class User {
        
        public static Map<String, Object> createDefaultRegisterRequest() {
            return UserFixtures.createDefaultRegisterRequest();
        }
        
        public static Map<String, Object> createRegisterRequest(
                String email,
                String password,
                String firstName,
                String lastName) {

            return UserFixtures.createRegisterRequest(email, password, firstName, lastName);
        }
        
        public static String generateUniqueEmail() {
            return UserFixtures.generateUniqueEmail();
        }
        
        public static Map<String, Object> createProfileUpdateRequest(
                String firstName, String lastName, String bio) {
            return UserFixtures.createProfileUpdateRequest(firstName, lastName, bio);
        }
        
        public static Map<String, Object> createInvalidEmailRequest() {
            return UserFixtures.createInvalidEmailRequest();
        }
        
        public static Map<String, Object> createWeakPasswordRequest() {
            return UserFixtures.createWeakPasswordRequest();
        }
        
        public static Map<String, Object> createIncompleteRequest() {
            return UserFixtures.createIncompleteRequest();
        }
    }
    
    /**
     * Authentication fixtures for creating auth requests.
     */
    @UtilityClass
    public static class Auth {
        
        public static Map<String, Object> createLoginRequest(String email, String password) {
            return AuthFixtures.createLoginRequest(email, password);
        }
        
        public static Map<String, Object> createTokenRefreshRequest(String refreshToken) {
            return AuthFixtures.createTokenRefreshRequest(refreshToken);
        }
        
        public static Map<String, Object> createInvalidLoginRequest() {
            return AuthFixtures.createInvalidLoginRequest();
        }
        
        public static Map<String, Object> createMissingEmailLoginRequest() {
            return AuthFixtures.createMissingEmailLoginRequest();
        }
        
        public static Map<String, Object> createMissingPasswordLoginRequest() {
            return AuthFixtures.createMissingPasswordLoginRequest();
        }
        
        public static Map<String, Object> createEmptyFieldsLoginRequest() {
            return AuthFixtures.createEmptyFieldsLoginRequest();
        }
    }
    
    /**
     * Common fixtures and constants.
     */
    @UtilityClass
    public static class Common {
        public static final String DEFAULT_PASSWORD = AuthFixtures.DEFAULT_PASSWORD;
    }
}