package org.readtogether.security.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.security.entity.InvalidTokenEntity;

import java.time.Instant;

@UtilityClass
public class InvalidTokenEntityFixtures {

    public static InvalidTokenEntity createInvalidTokenEntity(String tokenId, String reason, Instant createdAt) {
        return InvalidTokenEntity.builder()
                .tokenId(tokenId)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
    
    public static InvalidTokenEntity createInvalidTokenEntity(String tokenId) {
        Instant now = Instant.now();
        return InvalidTokenEntity.builder()
                .tokenId(tokenId)
                .createdAt(now)
                .updatedAt(now)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
    
    public static InvalidTokenEntity createDefaultInvalidTokenEntity() {
        Instant now = Instant.now();
        return InvalidTokenEntity.builder()
                .tokenId("test-token-id-" + System.currentTimeMillis())
                .createdAt(now)
                .updatedAt(now)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}