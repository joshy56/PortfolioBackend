package io.github.joshy56.portfoliobackend.security.repository;

import io.github.joshy56.portfoliobackend.security.entity.SimpleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author joshy56
 * @since 15/5/2023
 */
@Repository
public interface UserRepository extends JpaRepository<SimpleUser, UUID> {
    Optional<SimpleUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
