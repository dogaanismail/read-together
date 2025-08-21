package org.readtogether.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.readtogether.common.enums.TokenClaims.USER_ID;

public final class AuthenticationUtils {

    private AuthenticationUtils() {
        // Utility class - prevent instantiation
    }

    public static UUID extractUserIdFromAuth(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication cannot be null");
        }

        try {
            // Extract user ID from JWT token claims
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                Object userIdClaim = jwt.getClaim(USER_ID.getValue());
                if (userIdClaim != null) {
                    return UUID.fromString(userIdClaim.toString());
                }
            }

            // Fallback to authentication name if available
            String authName = authentication.getName();
            if (authName != null && !authName.equals("anonymousUser")) {
                return UUID.fromString(authName);
            }

            throw new IllegalArgumentException("Unable to extract user ID from authentication");

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format in authentication: " + e.getMessage());
        }
    }
}
