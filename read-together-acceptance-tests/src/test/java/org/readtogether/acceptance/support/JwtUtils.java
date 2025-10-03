package org.readtogether.acceptance.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

/**
 * JWT token utilities for acceptance tests.
 * Provides methods for parsing and validating JWT tokens.
 */
@Slf4j
@UtilityClass
public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse JWT token and extract payload claims.
     */
    public static JsonNode parseTokenPayload(String token) {
        try {
            // JWT format: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            // Decode the payload (second part)
            String payload = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes);

            return objectMapper.readTree(decodedPayload);
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    /**
     * Extract user ID from a JWT token.
     */
    public static String extractUserId(String token) {
        JsonNode payload = parseTokenPayload(token);
        JsonNode userIdNode = payload.get("userId");
        return userIdNode != null ? userIdNode.asText() : null;
    }

    /**
     * Extract user type from JWT token.
     */
    public static String extractUserType(String token) {
        JsonNode payload = parseTokenPayload(token);
        JsonNode userTypeNode = payload.get("userType");
        return userTypeNode != null ? userTypeNode.asText() : null;
    }

    /**
     * Extract expiration time from the JWT token.
     */
    public static Long extractExpirationTime(String token) {
        JsonNode payload = parseTokenPayload(token);
        JsonNode expNode = payload.get("exp");
        return expNode != null ? expNode.asLong() : null;
    }

    /**
     * Extract issued at time from JWT token.
     */
    public static Long extractIssuedAt(String token) {
        JsonNode payload = parseTokenPayload(token);
        JsonNode iatNode = payload.get("iat");
        return iatNode != null ? iatNode.asLong() : null;
    }

    /**
     * Check if the token is expired based on the current time.
     */
    public static boolean isTokenExpired(String token) {
        Long expirationTime = extractExpirationTime(token);
        if (expirationTime == null) {
            return false; // No expiration time found
        }

        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return currentTimeSeconds > expirationTime;
    }

    /**
     * Validate token structure (basic format check).
     */
    public static boolean hasValidStructure(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            String[] parts = token.split("\\.");
            return parts.length == 3 &&
                    !parts[0].isEmpty() &&
                    !parts[1].isEmpty() &&
                    !parts[2].isEmpty();
        } catch (Exception e) {
            log.debug("Token structure validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract claim value from a JWT token.
     */
    public static String extractClaim(String token, String claimName) {
        JsonNode payload = parseTokenPayload(token);
        JsonNode claimNode = payload.get(claimName);
        return claimNode != null ? claimNode.asText() : null;
    }

    /**
     * Get all claims from JWT token as JsonNode.
     */
    public static JsonNode getAllClaims(String token) {
        return parseTokenPayload(token);
    }
}