package org.readtogether.library.repository;

import org.readtogether.library.entity.BookSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookSessionRepository extends JpaRepository<BookSessionEntity, UUID> {

    List<BookSessionEntity> findByBookIdOrderByCreatedAtDesc(
            UUID bookId);

    List<BookSessionEntity> findByUserIdOrderByCreatedAtDesc(
            UUID userId);

    List<BookSessionEntity> findByUserIdAndBookIdOrderByCreatedAtDesc(
            UUID userId,
            UUID bookId);

    List<BookSessionEntity> findBySessionId(
            UUID sessionId);

    @Query("SELECT bs FROM book_session bs WHERE bs.userId = :userId AND bs.createdAt >= :sinceDate " +
            "ORDER BY bs.createdAt DESC")
    List<BookSessionEntity> findRecentSessionsByUserId(
            @Param("userId") UUID userId,
            @Param("sinceDate") Instant sinceDate);

    @Query("SELECT SUM(bs.readingTimeSeconds) FROM book_session bs WHERE bs.bookId = :bookId AND bs.userId = :userId")
    Long getTotalReadingTimeForUserBook(
            @Param("userId") UUID userId,
            @Param("bookId") UUID bookId);

    @Query("SELECT SUM(bs.pagesRead) FROM book_session bs WHERE bs.bookId = :bookId AND bs.userId = :userId")
    Integer getTotalPagesReadForUserBook(
            @Param("userId") UUID userId,
            @Param("bookId") UUID bookId);

    @Query("SELECT COUNT(bs) FROM book_session bs WHERE bs.bookId = :bookId AND bs.userId = :userId")
    long countSessionsForUserBook(
            @Param("userId") UUID userId,
            @Param("bookId") UUID bookId);

    boolean existsBySessionId(
            UUID sessionId);

    void deleteBySessionId(
            UUID sessionId);
}
