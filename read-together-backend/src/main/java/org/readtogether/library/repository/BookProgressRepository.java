package org.readtogether.library.repository;

import org.readtogether.library.entity.BookProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookProgressRepository extends JpaRepository<BookProgressEntity, UUID> {

    Optional<BookProgressEntity> findByUserIdAndBookId(UUID userId, UUID bookId);

    List<BookProgressEntity> findByUserIdOrderByLastReadAtDesc(UUID userId);

    List<BookProgressEntity> findByUserIdAndIsFavoriteTrueOrderByLastReadAtDesc(UUID userId);

    @Query("SELECT bp FROM book_progress bp WHERE bp.userId = :userId AND bp.status = 'IN_PROGRESS' " +
           "ORDER BY bp.lastReadAt DESC")
    List<BookProgressEntity> findCurrentlyReadingByUserId(@Param("userId") UUID userId);

    @Query("SELECT bp FROM book_progress bp WHERE bp.userId = :userId AND bp.status = 'COMPLETED' " +
           "ORDER BY bp.completedAt DESC")
    List<BookProgressEntity> findCompletedBooksByUserId(@Param("userId") UUID userId);

    @Query("SELECT bp FROM book_progress bp WHERE bp.userId = :userId AND " +
           "bp.lastReadAt >= :sinceDate ORDER BY bp.lastReadAt DESC")
    List<BookProgressEntity> findRecentlyReadBooks(@Param("userId") UUID userId,
                                                   @Param("sinceDate") Instant sinceDate);

    @Query("SELECT SUM(bp.totalReadingTimeSeconds) FROM book_progress bp WHERE bp.userId = :userId")
    Long getTotalReadingTimeByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(bp) FROM book_progress bp WHERE bp.userId = :userId AND bp.status = 'COMPLETED'")
    long countCompletedBooksByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(bp) FROM book_progress bp WHERE bp.userId = :userId AND bp.status = 'IN_PROGRESS'")
    long countInProgressBooksByUserId(@Param("userId") UUID userId);

    @Query("SELECT AVG(bp.progressPercentage) FROM book_progress bp WHERE bp.userId = :userId AND bp.status = 'IN_PROGRESS'")
    Double getAverageProgressByUserId(@Param("userId") UUID userId);

    void deleteByUserIdAndBookId(UUID userId, UUID bookId);
}
