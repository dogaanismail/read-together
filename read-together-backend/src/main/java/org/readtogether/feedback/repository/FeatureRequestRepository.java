package org.readtogether.feedback.repository;

import org.readtogether.feedback.common.enums.FeatureRequestCategory;
import org.readtogether.feedback.common.enums.FeatureRequestStatus;
import org.readtogether.feedback.entity.FeatureRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FeatureRequestRepository extends JpaRepository<FeatureRequestEntity, UUID> {

    Page<FeatureRequestEntity> findByStatusOrderByVotesDescCreatedAtDesc(
            FeatureRequestStatus status, 
            Pageable pageable
    );

    Page<FeatureRequestEntity> findByCategoryOrderByVotesDescCreatedAtDesc(
            FeatureRequestCategory category, 
            Pageable pageable
    );

    Page<FeatureRequestEntity> findByCategoryAndStatusOrderByVotesDescCreatedAtDesc(
            FeatureRequestCategory category, 
            FeatureRequestStatus status, 
            Pageable pageable
    );

    Page<FeatureRequestEntity> findAllByOrderByVotesDescCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE feature_request f SET f.votes = f.votes + 1 WHERE f.id = :id")
    void incrementVotes(@Param("id") UUID id);

    @Query("SELECT COUNT(f) FROM feature_request f WHERE f.status = :status")
    int countByStatus(@Param("status") FeatureRequestStatus status);

    @Query("SELECT COUNT(f) FROM feature_request f")
    int countTotal();

}