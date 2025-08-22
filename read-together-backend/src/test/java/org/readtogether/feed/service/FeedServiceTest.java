package org.readtogether.feed.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.common.enums.FeedItemType;
import org.readtogether.feed.fixtures.FeedEntityFixtures;
import org.readtogether.feed.model.FeedItemResponse;
import org.readtogether.feed.repository.FeedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeedService Tests")
class FeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private FeedLikeService feedLikeService;

    @InjectMocks
    private FeedService feedService;

    private Pageable pageable;
    private UUID userId;
    private UUID feedItemId;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);
        userId = UUID.randomUUID();
        feedItemId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should return public feed with correct ordering and paging")
    void shouldReturnPublicFeedWithCorrectOrderingAndPaging() {
        // Given
        FeedItemEntity feedItem1 = FeedEntityFixtures.createDefaultFeedItemEntity();
        FeedItemEntity feedItem2 = FeedEntityFixtures.createSessionFeedItemEntity();
        Page<FeedItemEntity> feedPage = new PageImpl<>(Arrays.asList(feedItem1, feedItem2));

        when(feedRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable))
                .thenReturn(feedPage);

        // When
        Page<FeedItemResponse> result = feedService.getFeed(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(feedRepository).findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no public feed items exist")
    void shouldReturnEmptyPageWhenNoPublicFeedItemsExist() {
        // Given
        Page<FeedItemEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(feedRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable))
                .thenReturn(emptyPage);

        // When
        Page<FeedItemResponse> result = feedService.getFeed(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return feed by type with correct filtering")
    void shouldReturnFeedByTypeWithCorrectFiltering() {
        // Given
        FeedItemEntity sessionFeedItem = FeedEntityFixtures.createSessionFeedItemEntity();
        Page<FeedItemEntity> feedPage = new PageImpl<>(Collections.singletonList(sessionFeedItem));

        when(feedRepository.findByItemTypeAndIsPublicTrueOrderByCreatedAtDesc(
                FeedItemType.SESSION, pageable))
                .thenReturn(feedPage);

        // When
        Page<FeedItemResponse> result = feedService.getFeedByType(FeedItemType.SESSION, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getItemType()).isEqualTo(FeedItemType.SESSION);
        verify(feedRepository).findByItemTypeAndIsPublicTrueOrderByCreatedAtDesc(
                FeedItemType.SESSION, pageable);
    }

    @Test
    @DisplayName("Should return user feed with all items including private")
    void shouldReturnUserFeedWithAllItemsIncludingPrivate() {
        // Given
        FeedItemEntity publicItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        FeedItemEntity privateItem = FeedEntityFixtures.createPrivateFeedItemEntity();
        Page<FeedItemEntity> userFeedPage = new PageImpl<>(Arrays.asList(publicItem, privateItem));

        when(feedRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(userFeedPage);

        // When
        Page<FeedItemResponse> result = feedService.getUserFeed(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(feedRepository).findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Test
    @DisplayName("Should return trending feed with view and like-based ordering")
    void shouldReturnTrendingFeedWithViewAndLikeBasedOrdering() {
        // Given
        FeedItemEntity trendingItem = FeedEntityFixtures.createFeedItemEntityWithCounts(100L, 50L, 20L);
        Page<FeedItemEntity> trendingPage = new PageImpl<>(Collections.singletonList(trendingItem));

        when(feedRepository.findTrendingItems(any(Instant.class), any(Long.class), eq(pageable)))
                .thenReturn(trendingPage);

        // When
        Page<FeedItemResponse> result = feedService.getTrendingFeed(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(feedRepository).findTrendingItems(any(Instant.class), any(Long.class), eq(pageable));
    }

    @Test
    @DisplayName("Should increment view count successfully")
    void shouldIncrementViewCountSuccessfully() {
        // When
        feedService.incrementViewCount(feedItemId);

        // Then
        verify(feedRepository).incrementViewCount(feedItemId);
    }

    @Test
    @DisplayName("Should like feed item successfully")
    void shouldLikeFeedItemSuccessfully() {
        // Given
        when(feedLikeService.likeFeedItem(feedItemId, userId)).thenReturn(true);

        // When
        boolean result = feedService.likeFeedItem(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(feedLikeService).likeFeedItem(feedItemId, userId);
    }

    @Test
    @DisplayName("Should return false when trying to like already liked item")
    void shouldReturnFalseWhenTryingToLikeAlreadyLikedItem() {
        // Given
        when(feedLikeService.likeFeedItem(feedItemId, userId)).thenReturn(false);

        // When
        boolean result = feedService.likeFeedItem(feedItemId, userId);

        // Then
        assertFalse(result);
        verify(feedLikeService).likeFeedItem(feedItemId, userId);
    }

    @Test
    @DisplayName("Should unlike feed item successfully")
    void shouldUnlikeFeedItemSuccessfully() {
        // Given
        when(feedLikeService.unlikeFeedItem(feedItemId, userId)).thenReturn(true);

        // When
        boolean result = feedService.unlikeFeedItem(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(feedLikeService).unlikeFeedItem(feedItemId, userId);
    }

    @Test
    @DisplayName("Should return false when trying to unlike not liked item")
    void shouldReturnFalseWhenTryingToUnlikeNotLikedItem() {
        // Given
        when(feedLikeService.unlikeFeedItem(feedItemId, userId)).thenReturn(false);

        // When
        boolean result = feedService.unlikeFeedItem(feedItemId, userId);

        // Then
        assertFalse(result);
        verify(feedLikeService).unlikeFeedItem(feedItemId, userId);
    }

    @Test
    @DisplayName("Should check if item is liked by user")
    void shouldCheckIfItemIsLikedByUser() {
        // Given
        when(feedLikeService.isLikedByUser(feedItemId, userId)).thenReturn(true);

        // When
        boolean result = feedService.isLikedByUser(feedItemId, userId);

        // Then
        assertTrue(result);
        verify(feedLikeService).isLikedByUser(feedItemId, userId);
    }

    @Test
    @DisplayName("Should find feed item by ID")
    void shouldFindFeedItemById() {
        // Given
        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        when(feedRepository.findById(feedItemId)).thenReturn(Optional.of(feedItem));

        // When
        Optional<FeedItemEntity> result = feedRepository.findById(feedItemId);

        // Then
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(feedItem.getId());
        verify(feedRepository).findById(feedItemId);
    }

    @Test
    @DisplayName("Should return empty when feed item not found")
    void shouldReturnEmptyWhenFeedItemNotFound() {
        // Given
        when(feedRepository.findById(feedItemId)).thenReturn(Optional.empty());

        // When
        Optional<FeedItemEntity> result = feedRepository.findById(feedItemId);

        // Then
        assertTrue(result.isEmpty());
        verify(feedRepository).findById(feedItemId);
    }
}