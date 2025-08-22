package org.readtogether.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.entity.FeedLikeEntity;
import org.readtogether.feed.factory.FeedLikeEntityFactory;

import org.readtogether.feed.repository.FeedLikeRepository;
import org.readtogether.feed.repository.FeedRepository;
import org.readtogether.notification.service.NotificationService;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;
    private final FeedRepository feedRepository;
    private final SessionRepository sessionRepository;
    private final NotificationService notificationService;

    @Transactional
    public boolean likeFeedItem(UUID feedItemId, UUID userId) {
        // Check if already liked
        if (feedLikeRepository.existsByFeedItemIdAndUserId(feedItemId, userId)) {
            log.debug("User {} has already liked feed item {}", userId, feedItemId);
            return false;
        }

        // Create like
        FeedLikeEntity like = FeedLikeEntityFactory.createFeedLike(feedItemId, userId);
        feedLikeRepository.save(like);

        // Update like count in feed item
        feedRepository.incrementLikeCount(feedItemId);

        // Send notification if this is a session
        sendLikeNotificationIfSession(feedItemId, userId);

        log.info("User {} liked feed item {}", userId, feedItemId);
        return true;
    }

    @Transactional
    public boolean unlikeFeedItem(UUID feedItemId, UUID userId) {
        Optional<FeedLikeEntity> likeOpt = feedLikeRepository.findByFeedItemIdAndUserId(feedItemId, userId);
        
        if (likeOpt.isEmpty()) {
            log.debug("User {} has not liked feed item {}", userId, feedItemId);
            return false;
        }

        // Remove like
        feedLikeRepository.delete(likeOpt.get());

        // Update like count in feed item
        feedRepository.decrementLikeCount(feedItemId);

        log.info("User {} unliked feed item {}", userId, feedItemId);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(UUID feedItemId, UUID userId) {
        return feedLikeRepository.existsByFeedItemIdAndUserId(feedItemId, userId);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(UUID feedItemId) {
        return feedLikeRepository.countByFeedItemId(feedItemId);
    }

    private void sendLikeNotificationIfSession(UUID feedItemId, UUID likerUserId) {
        Optional<FeedItemEntity> feedItemOpt = feedRepository.findById(feedItemId);
        if (feedItemOpt.isEmpty()) {
            return;
        }

        FeedItemEntity feedItem = feedItemOpt.get();
        if (feedItem.getItemType() == FeedItemEntity.FeedItemType.SESSION) {
            Optional<SessionEntity> sessionOpt = sessionRepository.findById(feedItem.getReferenceId());
            if (sessionOpt.isPresent()) {
                SessionEntity session = sessionOpt.get();
                // Don't notify if user likes their own content
                if (!session.getUserId().equals(likerUserId)) {
                    notificationService.notifySessionLiked(session.getUserId(), likerUserId, session);
                }
            }
        }
    }
}