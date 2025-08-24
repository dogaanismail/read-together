package org.readtogether.security.fixtures;

import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;
import org.readtogether.security.common.enums.TokenClaims;
import org.readtogether.user.common.enums.UserType;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static org.readtogether.security.common.enums.TokenType.BEARER;

@UtilityClass
public class TokenFixtures {

    public static String createValidAccessToken(String userId, Set<UserType> roles, Date expiresAt, PrivateKey privateKey) {
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
                .signWith(privateKey)
                .claims(claims)
                .compact();
    }
    
    public static String createExpiredToken(PrivateKey privateKey) {
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
                .signWith(privateKey)
                .claims(claims)
                .compact();
    }
    
    public static String createMalformedToken() {
        return "malformed.jwt.token";
    }
    
    public static String createInvalidSignatureToken() {
        // Use a different key pair than what will be used for verification
        KeyPair differentKeyPair = KeyFixtures.generateTestRsaKeyPair();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaims.USER_ID.getValue(), UUID.randomUUID().toString());
        claims.put(TokenClaims.USER_TYPE.getValue(), UserType.USER.name());
        
        // Sign with a different key than what will be used for verification
        return Jwts.builder()
                .header()
                .type(BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour from now
                .signWith(differentKeyPair.getPrivate()) // This will cause signature verification to fail
                .claims(claims)
                .compact();
    }
    
    public static String createRefreshToken(String userId, PrivateKey privateKey) {
        Date expiresAt = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // 7 days
        
        return Jwts.builder()
                .header()
                .type(BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(expiresAt)
                .signWith(privateKey)
                .claim(TokenClaims.USER_ID.getValue(), userId)
                .compact();
    }
}