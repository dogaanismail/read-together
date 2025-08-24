package org.readtogether.security.fixtures;

import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;
import org.readtogether.security.common.enums.TokenClaims;
import org.readtogether.user.common.enums.UserType;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static org.readtogether.security.common.enums.TokenType.BEARER;

@UtilityClass
public class TokenFixtures {

    public static String createValidAccessToken(String userId, Set<UserType> roles, Date expiresAt) {
        KeyPair keyPair = generateTestRsaKeyPair();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaims.USER_ID.getValue(), userId);
        claims.put(TokenClaims.USER_TYPE.getValue(), roles.iterator().next().name());
        
        return Jwts.builder()
                .header()
                .type(BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(expiresAt)
                .signWith(keyPair.getPrivate())
                .claims(claims)
                .compact();
    }
    
    public static String createExpiredToken() {
        KeyPair keyPair = generateTestRsaKeyPair();
        Date expiredDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
        
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaims.USER_ID.getValue(), UUID.randomUUID().toString());
        claims.put(TokenClaims.USER_TYPE.getValue(), UserType.USER.name());
        
        return Jwts.builder()
                .header()
                .type(BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 120)) // 2 hours ago
                .expiration(expiredDate)
                .signWith(keyPair.getPrivate())
                .claims(claims)
                .compact();
    }
    
    public static String createMalformedToken() {
        return "malformed.jwt.token";
    }
    
    public static String createInvalidSignatureToken() {
        KeyPair keyPair1 = generateTestRsaKeyPair();
        KeyPair keyPair2 = generateTestRsaKeyPair(); // Different key pair
        
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaims.USER_ID.getValue(), UUID.randomUUID().toString());
        claims.put(TokenClaims.USER_TYPE.getValue(), UserType.USER.name());
        
        // Sign with one key but will be verified with a different key
        return Jwts.builder()
                .header()
                .type(BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour from now
                .signWith(keyPair1.getPrivate()) // This will cause signature verification to fail
                .claims(claims)
                .compact();
    }
    
    public static String createRefreshToken(String userId) {
        KeyPair keyPair = generateTestRsaKeyPair();
        Date expiresAt = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // 7 days
        
        return Jwts.builder()
                .header()
                .type(BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(expiresAt)
                .signWith(keyPair.getPrivate())
                .claim(TokenClaims.USER_ID.getValue(), userId)
                .compact();
    }
    
    public static KeyPair generateTestRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate test RSA key pair", e);
        }
    }
    
    public static PublicKey getTestPublicKey() {
        return generateTestRsaKeyPair().getPublic();
    }
    
    public static PrivateKey getTestPrivateKey() {
        return generateTestRsaKeyPair().getPrivate();
    }
}