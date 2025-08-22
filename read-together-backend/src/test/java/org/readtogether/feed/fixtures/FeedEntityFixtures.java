package org.readtogether.feed.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.common.enums.FeedItemType;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class FeedEntityFixtures {

    public static final UUID DEFAULT_FEED_ITEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    public static final UUID SECONDARY_FEED_ITEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440011");
    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID SECONDARY_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID DEFAULT_REFERENCE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");

    public static FeedItemEntity createDefaultFeedItemEntity() {

        return createFeedItemEntity(
                DEFAULT_FEED_ITEM_ID,
                DEFAULT_USER_ID,
                FeedItemType.SESSION,
                DEFAULT_REFERENCE_ID,
                "Test Reading Session",
                "A practice reading session"
        );
    }

    public static FeedItemEntity createSessionFeedItemEntity() {

        return createFeedItemEntity(
                SECONDARY_FEED_ITEM_ID,
                SECONDARY_USER_ID,
                FeedItemType.SESSION,
                UUID.randomUUID(),
                "Another Reading Session",
                "Another practice reading session"
        );
    }

    public static FeedItemEntity createAchievementFeedItemEntity() {

        return createFeedItemEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                FeedItemType.ACHIEVEMENT,
                UUID.randomUUID(),
                "Achievement Unlocked: First Session",
                "Completed first reading session"
        );
    }

    public static FeedItemEntity createFeedItemEntity(
            UUID id,
            UUID userId,
            FeedItemType itemType,
            UUID referenceId,
            String title,
            String description) {

        return FeedItemEntity.builder()
                .id(id)
                .userId(userId)
                .itemType(itemType)
                .referenceId(referenceId)
                .title(title)
                .description(description)
                .mediaUrl("https://example.com/media.mp4")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .isPublic(true)
                .viewCount(10L)
                .likeCount(5L)
                .commentCount(3L)
                .metadata("{\"duration\": 300}")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static FeedItemEntity createPrivateFeedItemEntity() {

        FeedItemEntity baseItem = createFeedItemEntity(
                UUID.randomUUID(),
                DEFAULT_USER_ID,
                FeedItemType.SESSION,
                UUID.randomUUID(),
                "Private Reading Session",
                "A private practice session"
        );

        return FeedItemEntity.builder()
                .id(baseItem.getId())
                .userId(baseItem.getUserId())
                .itemType(baseItem.getItemType())
                .referenceId(baseItem.getReferenceId())
                .title(baseItem.getTitle())
                .description(baseItem.getDescription())
                .mediaUrl(baseItem.getMediaUrl())
                .thumbnailUrl(baseItem.getThumbnailUrl())
                .isPublic(false)
                .viewCount(baseItem.getViewCount())
                .likeCount(baseItem.getLikeCount())
                .commentCount(baseItem.getCommentCount())
                .metadata(baseItem.getMetadata())
                .createdAt(baseItem.getCreatedAt())
                .updatedAt(baseItem.getUpdatedAt())
                .build();
    }

    public static FeedItemEntity createFeedItemEntityWithCounts(
            long viewCount,
            long likeCount,
            long commentCount) {

        FeedItemEntity baseItem = createDefaultFeedItemEntity();
        return FeedItemEntity.builder()
                .id(baseItem.getId())
                .userId(baseItem.getUserId())
                .itemType(baseItem.getItemType())
                .referenceId(baseItem.getReferenceId())
                .title(baseItem.getTitle())
                .description(baseItem.getDescription())
                .mediaUrl(baseItem.getMediaUrl())
                .thumbnailUrl(baseItem.getThumbnailUrl())
                .isPublic(baseItem.isPublic())
                .viewCount(viewCount)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .metadata(baseItem.getMetadata())
                .createdAt(baseItem.getCreatedAt())
                .updatedAt(baseItem.getUpdatedAt())
                .build();
    }
}