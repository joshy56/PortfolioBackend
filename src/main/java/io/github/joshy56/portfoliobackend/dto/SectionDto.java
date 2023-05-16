package io.github.joshy56.portfoliobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * @author joshy56
 * @since 14/5/2023
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionDto {
    private String title;
    private Double weight;
    private List<UUID> cards;
}
