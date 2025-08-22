package org.readtogether.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.utils.SecurityUtils;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(
            name = "created_by",
            nullable = false,
            updatable = false,
            columnDefinition = "varchar(255) default 'anonymousUser'"
    )
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(
            name = "updated_by",
            nullable = false
    )
    private String updatedBy;

    @Column(name = "deleted_at", updatable = false)
    private Instant deletedAt;

    @Column(name = "deleted_by", updatable = false)
    private String deletedBy;

    @Column(name = "deleted_reason")
    private String deletedReason;

    @Version
    @Column(
            name = "version",
            nullable = false,
            columnDefinition = "integer default 0"
    )
    private short version;

    @PrePersist
    public void prePersist() {

        this.createdBy = SecurityUtils.getCurrentUserEmail();
        this.createdAt = Instant.now();

        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtils.getCurrentUserEmail();
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedBy = SecurityUtils.getCurrentUserEmail();
        this.updatedAt = Instant.now();
    }

    @PreRemove
    public void preRemove() {

        this.deletedBy = SecurityUtils.getCurrentUserEmail();
        this.deletedAt = Instant.now();
    }

}
