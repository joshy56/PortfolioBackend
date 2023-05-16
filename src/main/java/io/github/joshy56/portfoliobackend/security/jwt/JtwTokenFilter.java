package io.github.joshy56.portfoliobackend.security.jwt;

import io.github.joshy56.portfoliobackend.security.dto.JwtDto;
import io.github.joshy56.portfoliobackend.security.service.SimpleUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

/**
 * @author joshy56
 * @since 15/5/2023
 */
@Component
public class JtwTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JtwTokenFilter.class);

    @Autowired
    private JwtProvider provider;
    @Autowired
    private SimpleUserDetailsService userDetailsService;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        try {
            if (token != null && provider.validateToken(token)) {
                String username = provider.getUsernameFromToken(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }catch (Exception e) {
            try {
                String freshToken = provider.refreshToken(new JwtDto(token, null));
                String username = provider.getUsernameFromToken(freshToken);
                UserDetails user = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ParseException ex) {
                logger.error("Something wrong with login", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> !header.isBlank())
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.replace("Bearer ", ""))
                .orElse(null);
    }
}
