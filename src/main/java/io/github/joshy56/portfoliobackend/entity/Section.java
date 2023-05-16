package io.github.joshy56.portfoliobackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * @author joshy56
 * @since 13/5/2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifier;
    private String title;
    @OneToMany
    private List<Card> cards;
}
