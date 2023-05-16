package io.github.joshy56.portfoliobackend.security.dto;

import lombok.*;

/**
 * @author joshy56
 * @since 15/5/2023
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
    private String token;
}
