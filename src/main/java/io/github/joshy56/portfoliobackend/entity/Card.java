package io.github.joshy56.portfoliobackend.entity;

import jakarta.persistence.*;
import lombok.*;

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
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifier;
    private int knowledgeOnTech;
    private String avatarHeaderImage;
    private String headerTitle;
    private String headerSubtitle;
    private String bodyTitle;
    private String bodySubtitle;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Section section;
}
