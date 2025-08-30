package org.readtogether.library.repository;

import org.readtogether.library.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

    Optional<BookEntity> findByIdAndAddedByUserId(
            UUID id,
            UUID userId);

    List<BookEntity> findByAddedByUserIdOrderByCreatedAtDesc(
            UUID userId);

    Page<BookEntity> findByAddedByUserIdOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable);

    List<BookEntity> findByIsPublicTrueOrderByCreatedAtDesc();

    Page<BookEntity> findByIsPublicTrueOrderByCreatedAtDesc(
            Pageable pageable);

    @Query("SELECT b FROM book b WHERE b.addedByUserId = :userId AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<BookEntity> findByUserIdAndSearchTerm(
            @Param("userId") UUID userId,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT b FROM book b WHERE b.isPublic = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<BookEntity> findPublicBooksBySearchTerm(@Param("searchTerm") String searchTerm);

    boolean existsByIsbnAndAddedByUserId(
            String isbn,
            UUID userId);

    long countByAddedByUserId(
            UUID userId);

    @Query("SELECT COUNT(b) FROM book b WHERE b.isPublic = true")
    long countPublicBooks();
}
