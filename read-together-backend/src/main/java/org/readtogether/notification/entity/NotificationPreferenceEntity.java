package org.readtogether.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "notification_preferences")
@Table(name = "notification_preferences")
public class NotificationPreferenceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private boolean emailNotifications = true;

    @Column(name = "push_notifications", nullable = false)
    @Builder.Default
    private boolean pushNotifications = true;

    @Column(name = "session_likes", nullable = false)
    @Builder.Default
    private boolean sessionLikes = true;

    @Column(name = "new_followers", nullable = false)
    @Builder.Default
    private boolean newFollowers = true;

    @Column(name = "live_stream_alerts", nullable = false)
    @Builder.Default
    private boolean liveStreamAlerts = true;

    @Column(name = "weekly_digest", nullable = false)
    @Builder.Default
    private boolean weeklyDigest = true;

    @Column(name = "marketing_emails", nullable = false)
    @Builder.Default
    private boolean marketingEmails = false;

    @Column(name = "upload_status", nullable = false)
    @Builder.Default
    private boolean uploadStatus = true;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "push_subscription_endpoint")
    private String pushSubscriptionEndpoint;

    @Column(name = "push_subscription_keys", columnDefinition = "TEXT")
    private String pushSubscriptionKeys; // JSON string for web push subscription

}
