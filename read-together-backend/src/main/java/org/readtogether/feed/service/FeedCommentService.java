package org.readtogether.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.feed.entity.FeedCommentEntity;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.factory.FeedCommentEntityFactory;
import org.readtogether.feed.factory.FeedCommentResponseFactory;
import org.readtogether.feed.model.CreateCommentRequest;
import org.readtogether.feed.model.FeedCommentResponse;
import org.readtogether.feed.repository.FeedCommentRepository;
import org.readtogether.feed.repository.FeedRepository;
import org.readtogether.notification.service.NotificationService;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentService {

    private final FeedCommentRepository feedCommentRepository;
    private final FeedRepository feedRepository;
    private final SessionRepository sessionRepository;
    private final NotificationService notificationService;

    @Transactional
    public FeedCommentResponse createComment(UUID feedItemId, UUID userId, CreateCommentRequest request) {
        // Create comment
        FeedCommentEntity comment = FeedCommentEntityFactory.createFeedComment(
                feedItemId, 
                userId, 
                request.getContent(), 
                request.getParentCommentId()
        );
        
        FeedCommentEntity savedComment = feedCommentRepository.save(comment);

        // Update comment count in feed item
        feedRepository.incrementCommentCount(feedItemId);

        // Send notification if this is a session comment
        sendCommentNotificationIfSession(feedItemId, userId, request.getContent());

        log.info("User {} commented on feed item {}", userId, feedItemId);
        
        return FeedCommentResponseFactory.createFromEntity(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<FeedCommentResponse> getComments(UUID feedItemId, Pageable pageable) {
        return feedCommentRepository.findTopLevelCommentsByFeedItemId(feedItemId, pageable)
                .map(FeedCommentResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public Page<FeedCommentResponse> getReplies(UUID parentCommentId, Pageable pageable) {
        return feedCommentRepository.findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(parentCommentId, pageable)
                .map(FeedCommentResponseFactory::createFromEntity);
    }

    @Transactional(readOnly = true)
    public long getCommentCount(UUID feedItemId) {
        return feedCommentRepository.countByFeedItemIdAndIsDeletedFalse(feedItemId);
    }

    @Transactional
    public boolean deleteComment(UUID commentId, UUID userId) {
        Optional<FeedCommentEntity> commentOpt = feedCommentRepository.findById(commentId);
        
        if (commentOpt.isEmpty()) {
            log.debug("Comment {} not found", commentId);
            return false;
        }

        FeedCommentEntity comment = commentOpt.get();
        
        // Check if user owns the comment
        if (!comment.getUserId().equals(userId)) {
            log.debug("User {} does not own comment {}", userId, commentId);
            return false;
        }

        // Soft delete
        comment.setDeleted(true);
        feedCommentRepository.save(comment);

        // Update comment count in feed item
        feedRepository.decrementCommentCount(comment.getFeedItemId());

        log.info("User {} deleted comment {}", userId, commentId);
        return true;
    }

    private void sendCommentNotificationIfSession(UUID feedItemId, UUID commenterUserId, String commentContent) {
        Optional<FeedItemEntity> feedItemOpt = feedRepository.findById(feedItemId);
        if (feedItemOpt.isEmpty()) {
            return;
        }

        FeedItemEntity feedItem = feedItemOpt.get();
        if (feedItem.getItemType() == FeedItemEntity.FeedItemType.SESSION) {
            Optional<SessionEntity> sessionOpt = sessionRepository.findById(feedItem.getReferenceId());
            if (sessionOpt.isPresent()) {
                SessionEntity session = sessionOpt.get();
                // Don't notify if user comments on their own content
                if (!session.getUserId().equals(commenterUserId)) {
                    notificationService.notifySessionCommented(session.getUserId(), commenterUserId, session, commentContent);
                }
            }
        }
    }
}