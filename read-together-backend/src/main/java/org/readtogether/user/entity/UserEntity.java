package org.readtogether.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.common.enums.TokenClaims;
import org.readtogether.common.model.auth.enums.UserStatus;
import org.readtogether.common.model.auth.enums.UserType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.readtogether.common.model.auth.enums.UserStatus.ACTIVE;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "user_status", nullable = false, length = 50)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = ACTIVE;

    @Column(name = "username", unique = true, length = 50)
    private String username;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "reading_streak")
    @Builder.Default
    private int readingStreak = 0;

    @Column(name = "total_sessions")
    @Builder.Default
    private long totalSessions = 0L;

    @Column(name = "total_reading_time_seconds")
    @Builder.Default
    private long totalReadingTimeSeconds = 0L;

    @Column(name = "longest_streak")
    @Builder.Default
    private int longestStreak = 0;

    @Column(name = "total_active_days")
    @Builder.Default
    private long totalActiveDays = 0L;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;

    public Map<String, Object> getClaims() {

        final Map<String, Object> claims = new HashMap<>();

        claims.put(TokenClaims.USER_ID.getValue(), this.id);
        claims.put(TokenClaims.USER_TYPE.getValue(), this.userType);
        claims.put(TokenClaims.USER_STATUS.getValue(), this.userStatus);
        claims.put(TokenClaims.USER_FIRST_NAME.getValue(), this.firstName);
        claims.put(TokenClaims.USER_LAST_NAME.getValue(), this.lastName);
        claims.put(TokenClaims.USER_EMAIL.getValue(), this.email);

        return claims;
    }

}
