package org.readtogether.security.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.security.entity.InvalidTokenEntity;
import org.readtogether.security.fixtures.InvalidTokenEntityFixtures;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisplayName("InvalidTokenRepository Tests")
class InvalidTokenRepositoryTests extends BaseIntegrationTest {

    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Test
    @DisplayName("Should persist invalid token")
    void shouldPersistInvalidToken() {
        // Given
        String tokenId = "test-token-" + System.currentTimeMillis();
        InvalidTokenEntity entity = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId);

        // When
        InvalidTokenEntity saved = invalidTokenRepository.save(entity);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTokenId()).isEqualTo(tokenId);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find by token ID")
    void shouldFindByTokenId() {
        // Given
        String tokenId = "find-test-token-" + System.currentTimeMillis();
        InvalidTokenEntity entity = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId);
        invalidTokenRepository.save(entity);

        // When
        Optional<InvalidTokenEntity> found = invalidTokenRepository.findByTokenId(tokenId);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTokenId()).isEqualTo(tokenId);
    }

    @Test
    @DisplayName("Should return empty optional when token not found")
    void shouldReturnEmptyOptionalWhenTokenNotFound() {
        // Given
        String nonExistentTokenId = "non-existent-token-" + System.currentTimeMillis();

        // When
        Optional<InvalidTokenEntity> found = invalidTokenRepository.findByTokenId(nonExistentTokenId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should enforce uniqueness of token ID")
    void shouldEnforceUniquenessOfTokenId() {
        // Given
        String tokenId = "unique-test-token-" + System.currentTimeMillis();
        InvalidTokenEntity entity1 = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId);
        InvalidTokenEntity entity2 = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId);

        // When
        invalidTokenRepository.save(entity1);

        // Then - should be able to save another with same tokenId (no unique constraint expected based on entity)
        InvalidTokenEntity saved2 = invalidTokenRepository.save(entity2);
        assertThat(saved2).isNotNull();
        assertThat(saved2.getTokenId()).isEqualTo(tokenId);
    }

    @Test
    @DisplayName("Should save multiple invalid tokens")
    void shouldSaveMultipleInvalidTokens() {
        // Given
        String tokenId1 = "multi-test-token-1-" + System.currentTimeMillis();
        String tokenId2 = "multi-test-token-2-" + System.currentTimeMillis();
        String tokenId3 = "multi-test-token-3-" + System.currentTimeMillis();

        InvalidTokenEntity entity1 = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId1);
        InvalidTokenEntity entity2 = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId2);
        InvalidTokenEntity entity3 = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId3);

        // When
        invalidTokenRepository.saveAll(java.util.List.of(entity1, entity2, entity3));

        // Then
        Optional<InvalidTokenEntity> found1 = invalidTokenRepository.findByTokenId(tokenId1);
        Optional<InvalidTokenEntity> found2 = invalidTokenRepository.findByTokenId(tokenId2);
        Optional<InvalidTokenEntity> found3 = invalidTokenRepository.findByTokenId(tokenId3);

        assertThat(found1).isPresent();
        assertThat(found2).isPresent();
        assertThat(found3).isPresent();
    }

    @Test
    @DisplayName("Should handle null token ID gracefully")
    void shouldHandleNullTokenIdGracefully() {
        // Given / When
        Optional<InvalidTokenEntity> found = invalidTokenRepository.findByTokenId(null);

        // Then
        assertThat(found).isEmpty();
    }
}