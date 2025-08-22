package org.readtogether.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.user.common.enums.ProfileVisibility;

import java.util.UUID;

import static org.readtogether.user.common.enums.ProfileVisibility.PUBLIC;


@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "privacy_settings")
@Table(name = "privacy_settings")
public class PrivacySettingsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false)
    @Builder.Default
    private ProfileVisibility profileVisibility = PUBLIC;

    @Column(name = "show_email", nullable = false)
    @Builder.Default
    private boolean showEmail = false;

    @Column(name = "show_online_status", nullable = false)
    @Builder.Default
    private boolean showOnlineStatus = true;

    @Column(name = "allow_messages", nullable = false)
    @Builder.Default
    private boolean allowMessages = true;

    @Column(name = "show_reading_sessions", nullable = false)
    @Builder.Default
    private boolean showReadingSessions = true;

    @Column(name = "searchable", nullable = false)
    @Builder.Default
    private boolean searchable = true;

}
