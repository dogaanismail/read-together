package org.readtogether.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.factory.FeedItemEntityFactory;
import org.readtogether.feed.factory.FeedItemResponseFactory;
import org.readtogether.feed.model.FeedItemResponse;
import org.readtogether.feed.repository.FeedRepository;
import org.readtogether.session.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    @Transactional(readOnly = true)
    public Page<FeedItemResponse> getFeed(Pageable pageable) {

        return feedRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
            .map(FeedItemResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<FeedItemResponse> getFeedByType(
            FeedItemEntity.FeedItemType itemType,
            Pageable pageable) {

        return feedRepository.findByItemTypeAndIsPublicTrueOrderByCreatedAtDesc(itemType, pageable)
            .map(FeedItemResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<FeedItemResponse> getUserFeed(
            UUID userId,
            Pageable pageable) {

        return feedRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(FeedItemResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<FeedItemResponse> getFeedWithFilters(
            FeedItemEntity.FeedItemType itemType,
            String search,
            String language,
            String sortBy,
            String sortDirection,
            Pageable pageable) {

        // This method should be implemented with custom repository queries
        // For now, using basic filtering - you'll need to implement custom queries
        if (itemType != null) {
            return feedRepository.findByItemTypeAndIsPublicTrueOrderByCreatedAtDesc(itemType, pageable)
                .map(FeedItemResponseFactory::createFromEntity);
        }

        return feedRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
            .map(FeedItemResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<FeedItemResponse> getTrendingFeed(Pageable pageable) {

        Instant since = Instant.now().minus(7, ChronoUnit.DAYS);
        return feedRepository.findTrendingItems(since, 5L, pageable)
            .map(FeedItemResponseFactory::createFromEntity);
    }

    @Transactional
    public FeedItemEntity createSessionFeedItem(SessionEntity session) {

        FeedItemEntity feedItem = FeedItemEntityFactory.createFromSession(session);
        return feedRepository.save(feedItem);
    }

    @Transactional
    public FeedItemEntity createAchievementFeedItem(
            UUID userId,
            UUID achievementId,
            String title,
            String description) {

        FeedItemEntity feedItem = FeedItemEntityFactory.createAchievementFeedItem(userId, achievementId, title, description);
        return feedRepository.save(feedItem);
    }

    @Transactional
    public FeedItemEntity createMilestoneFeedItem(
            UUID userId,
            UUID milestoneId,
            String title,
            String description) {

        FeedItemEntity feedItem = FeedItemEntityFactory.createMilestoneFeedItem(userId, milestoneId, title, description);
        return feedRepository.save(feedItem);
    }

    @Transactional
    public void incrementViewCount(UUID feedItemId) {

        feedRepository.incrementViewCount(feedItemId);
    }

    @Transactional
    public void likeFeedItem(UUID feedItemId) {

        feedRepository.incrementLikeCount(feedItemId);
    }

    @Transactional
    public void unlikeFeedItem(UUID feedItemId) {

        feedRepository.decrementLikeCount(feedItemId);
    }

    @Transactional
    public FeedItemEntity createFeedItemFromSession(SessionEntity session) {
        log.info("Creating feed item for session: {}", session.getId());

        FeedItemEntity feedItem = FeedItemEntity.builder()
            .userId(session.getUserId())
            .itemType(FeedItemEntity.FeedItemType.SESSION)
            .referenceId(session.getId())
            .title(session.getTitle())
            .description(session.getDescription())
            .mediaUrl(session.getMediaUrl())
            .thumbnailUrl(session.getThumbnailUrl())
            .isPublic(session.isPublic())
            .viewCount(0L)
            .likeCount(0L)
            .commentCount(0L)
            .build();

        return feedRepository.save(feedItem);
    }
}
