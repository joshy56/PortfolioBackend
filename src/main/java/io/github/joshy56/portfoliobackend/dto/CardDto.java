package io.github.joshy56.portfoliobackend.dto;

import lombok.*;

import java.util.UUID;

/**
 * @author joshy56
 * @since 13/5/2023
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {
    private int knowledgeOnTech;
    private String avatarHeaderImage;
    private String headerTitle;
    private String headerSubtitle;
    private String bodyTitle;
    private String bodySubtitle;
    private UUID sectionId;
}
