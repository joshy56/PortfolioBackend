package io.github.joshy56.portfoliobackend.security.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.github.joshy56.portfoliobackend.security.dto.JwtDto;
import io.github.joshy56.portfoliobackend.security.entity.SimpleUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * @author joshy56
 * @since 15/5/2023
 */
@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication auth) {
        SimpleUser user = (SimpleUser) auth.getPrincipal();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(generateSecret(secret))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSecret(secret))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(generateSecret(secret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Expired token", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported token", e);
        } catch (MalformedJwtException e) {
            logger.error("Malformed token", e);
        } catch (SignatureException e) {
            logger.error("No signed token", e);
        } catch (IllegalArgumentException e) {
            logger.error("Empty token", e);
        }
        return false;
    }

    public String refreshToken(JwtDto dto) throws ParseException {
        try {
            Jwts.parserBuilder().setSigningKey(generateSecret(secret)).build().parseClaimsJws(dto.getToken());
        } catch (ExpiredJwtException e) {
            JWT jwt = JWTParser.parse(dto.getToken());
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String username = claims.getSubject();

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                    .signWith(generateSecret(secret))
                    .compact();
        }
        return null;
    }

    private Key generateSecret(String secret) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
