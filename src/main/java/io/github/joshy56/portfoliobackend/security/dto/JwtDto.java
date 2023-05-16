package io.github.joshy56.portfoliobackend.security.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author joshy56
 * @since 15/5/2023
 */
@Getter
@Setter
public class JwtDto {
    private String token;
    private String bearer;
    private String username;

    public JwtDto(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
