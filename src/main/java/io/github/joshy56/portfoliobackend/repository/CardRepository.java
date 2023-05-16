package io.github.joshy56.portfoliobackend.repository;

import io.github.joshy56.portfoliobackend.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author joshy56
 * @since 13/5/2023
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
}
