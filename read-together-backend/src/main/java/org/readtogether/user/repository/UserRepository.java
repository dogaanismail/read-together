package org.readtogether.user.repository;

import org.readtogether.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(final String email);

    Optional<UserEntity> findByEmail(final String email);
}
