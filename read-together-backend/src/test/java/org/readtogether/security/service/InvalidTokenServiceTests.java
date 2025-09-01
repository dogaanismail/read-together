package org.readtogether.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.security.entity.InvalidTokenEntity;
import org.readtogether.security.exception.TokenAlreadyInvalidatedException;
import org.readtogether.security.fixtures.InvalidTokenEntityFixtures;
import org.readtogether.security.repository.InvalidTokenRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InvalidTokenService Tests")
class InvalidTokenServiceTests {

    @Mock
    private InvalidTokenRepository invalidTokenRepository;

    private InvalidTokenService invalidTokenService;

    @BeforeEach
    void setUp() {
        invalidTokenService = new InvalidTokenService(invalidTokenRepository);
    }

    @Test
    @DisplayName("Should invalidate token once")
    void shouldInvalidateTokenOnce() {
        // Given
        String tokenId = "test-token-id";
        Set<String> tokenIds = Set.of(tokenId);

        when(invalidTokenRepository.saveAll(any())).thenReturn(null);

        // When
        invalidTokenService.invalidateTokens(tokenIds);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<InvalidTokenEntity>> captor = ArgumentCaptor.forClass(Set.class);
        verify(invalidTokenRepository).saveAll(captor.capture());

        Set<InvalidTokenEntity> savedEntities = captor.getValue();
        assertThat(savedEntities).hasSize(1);

        InvalidTokenEntity savedEntity = savedEntities.iterator().next();
        assertThat(savedEntity.getTokenId()).isEqualTo(tokenId);
    }

    @Test
    @DisplayName("Should invalidate multiple tokens")
    void shouldInvalidateMultipleTokens() {
        // Given
        String tokenId1 = "test-token-id-1";
        String tokenId2 = "test-token-id-2";
        String tokenId3 = "test-token-id-3";
        Set<String> tokenIds = Set.of(tokenId1, tokenId2, tokenId3);

        when(invalidTokenRepository.saveAll(any())).thenReturn(null);

        // When
        invalidTokenService.invalidateTokens(tokenIds);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<InvalidTokenEntity>> captor = ArgumentCaptor.forClass(Set.class);
        verify(invalidTokenRepository).saveAll(captor.capture());

        Set<InvalidTokenEntity> savedEntities = captor.getValue();
        assertThat(savedEntities).hasSize(3);

        Set<String> savedTokenIds = savedEntities.stream()
                .map(InvalidTokenEntity::getTokenId)
                .collect(java.util.stream.Collectors.toSet());
        assertThat(savedTokenIds).containsExactlyInAnyOrder(tokenId1, tokenId2, tokenId3);
    }

    @Test
    @DisplayName("Should be idempotent when already invalidated")
    void shouldBeIdempotentWhenAlreadyInvalidated() {
        // Given
        String tokenId = "already-invalidated-token";
        InvalidTokenEntity existingEntity = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId);

        when(invalidTokenRepository.findByTokenId(tokenId)).thenReturn(Optional.of(existingEntity));

        // When / Then
        assertThatThrownBy(() -> invalidTokenService.checkForInvalidityOfToken(tokenId))
                .isInstanceOf(TokenAlreadyInvalidatedException.class)
                .hasMessageContaining(tokenId);

        verify(invalidTokenRepository).findByTokenId(tokenId);
    }

    @Test
    @DisplayName("Should check if token is invalidated - not found")
    void shouldCheckIfTokenIsInvalidatedNotFound() {
        // Given
        String tokenId = "valid-token";

        when(invalidTokenRepository.findByTokenId(tokenId)).thenReturn(Optional.empty());

        // When / Then - should not throw
        invalidTokenService.checkForInvalidityOfToken(tokenId);

        verify(invalidTokenRepository).findByTokenId(tokenId);
    }

    @Test
    @DisplayName("Should check if token is invalidated - found")
    void shouldCheckIfTokenIsInvalidatedFound() {
        // Given
        String tokenId = "invalidated-token";
        InvalidTokenEntity existingEntity = InvalidTokenEntityFixtures.createInvalidTokenEntity(tokenId);

        when(invalidTokenRepository.findByTokenId(tokenId)).thenReturn(Optional.of(existingEntity));

        // When / Then
        assertThatThrownBy(() -> invalidTokenService.checkForInvalidityOfToken(tokenId))
                .isInstanceOf(TokenAlreadyInvalidatedException.class)
                .hasMessageContaining(tokenId);

        verify(invalidTokenRepository).findByTokenId(tokenId);
    }

    @Test
    @DisplayName("Should handle empty token set for invalidation")
    void shouldHandleEmptyTokenSetForInvalidation() {
        // Given
        Set<String> emptyTokenIds = Set.of();

        // When
        invalidTokenService.invalidateTokens(emptyTokenIds);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<InvalidTokenEntity>> captor = ArgumentCaptor.forClass(Set.class);
        verify(invalidTokenRepository).saveAll(captor.capture());

        Set<InvalidTokenEntity> savedEntities = captor.getValue();
        assertThat(savedEntities).isEmpty();
    }
}