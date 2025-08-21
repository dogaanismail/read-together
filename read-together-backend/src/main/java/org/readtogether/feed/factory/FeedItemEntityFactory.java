package org.readtogether.feed.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.session.entity.SessionEntity;

import java.util.UUID;

import static org.readtogether.feed.entity.FeedItemEntity.FeedItemType.ACHIEVEMENT;
import static org.readtogether.feed.entity.FeedItemEntity.FeedItemType.MILESTONE;

@UtilityClass
public class FeedItemEntityFactory {

    public static FeedItemEntity createFromSession(SessionEntity session) {

        return FeedItemEntity.builder()
                .userId(session.getUserId())
                .itemType(FeedItemEntity.FeedItemType.SESSION)
                .referenceId(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .mediaUrl(session.getMediaUrl())
                .isPublic(session.isPublic())
                .metadata(createSessionMetadata(session))
                .build();
    }

    public static FeedItemEntity createAchievementFeedItem(
            UUID userId,
            UUID achievementId,
            String title,
            String description) {

        return FeedItemEntity.builder()
                .userId(userId)
                .itemType(ACHIEVEMENT)
                .referenceId(achievementId)
                .title(title)
                .description(description)
                .isPublic(true)
                .metadata(createAchievementMetadata())
                .build();
    }

    public static FeedItemEntity createMilestoneFeedItem(
            UUID userId,
            UUID milestoneId,
            String title,
            String description) {

        return FeedItemEntity.builder()
                .userId(userId)
                .itemType(MILESTONE)
                .referenceId(milestoneId)
                .title(title)
                .description(description)
                .isPublic(true)
                .metadata(createMilestoneMetadata())
                .build();
    }

    private static String createSessionMetadata(SessionEntity session) {

        return String.format(
            "{\"mediaType\":\"%s\",\"duration\":%d,\"fileSize\":%d}",
            session.getMediaType(),
            session.getDurationSeconds() != null ? session.getDurationSeconds() : 0,
            session.getFileSizeBytes() != null ? session.getFileSizeBytes() : 0
        );
    }

    private static String createAchievementMetadata() {

        return "{\"type\":\"achievement\"}";
    }

    private static String createMilestoneMetadata() {

        return "{\"type\":\"milestone\"}";
    }
}
