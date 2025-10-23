package com.darthchild.aurthor.security.JWT;

import com.darthchild.aurthor.security.model.UserPrincipal;
import com.darthchild.aurthor.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNullApi;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Intercepts each request, extracts accompanying JWT token, validates it,
 * then sets the Auth object in the SecurityContext so request can proceed
 * marked as <b>authenticated</b> ✅
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Define all paths that should skip JWT validation entirely
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth", "/swagger-ui", "/v3/api-docs"
    );

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * Determines whether the filter should be skipped for this request based on the request path.
     * @return true if the request path indicates an authentication endpoint, causing the filter to be bypassed; false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String token = jwtUtils.extractToken(request);

            // Skip processing if no token present (let request pass as unauthenticated)
            if (token == null || token.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtils.extractUsername(token);
            Authentication authStatus = SecurityContextHolder.getContext().getAuthentication();

            // Checks: if request isn't already authenticated & Token validity
            if (authStatus == null && jwtUtils.validateToken(token)) {

                UserPrincipal user = (UserPrincipal) userDetailsService.loadUserByUsername(username);

                // creates a new authenticated Auth obj
                UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

                // attach the "request" obj too
                upaToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store the authenticated user details (Auth obj) in the SecurityContext so
                // that downstream filters and controllers recognize this request as authenticated
                SecurityContextHolder.getContext().setAuthentication(upaToken);
            }
        } catch (Exception e) {
            logger.error("Could not set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}
