package com.petone.petone.config;

import com.petone.petone.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
            return true;

        return path.equals("/favicon.ico") ||
                matcher.match("/assets/**", path) ||
                matcher.match("/**/*.css", path) ||
                matcher.match("/**/*.js", path) ||
                matcher.match("/**/*.png", path) ||
                matcher.match("/**/*.jpg", path) ||
                matcher.match("/**/*.jpeg", path) ||
                matcher.match("/**/*.svg", path) ||
                matcher.match("/**/*.ico", path) ||

                matcher.match("/swagger-ui/**", path) ||
                matcher.match("/v3/api-docs/**", path) ||
                path.equals("/swagger-ui.html") ||

                matcher.match("/api/auth/**", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(7);
        String username = null;

        try {
            username = jwtUtil.getUsernameFromToken(jwt);
        } catch (Exception e) {
            logger.warn("Token JWT inválido/malformado.", e);
            chain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

                upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(upat);
            }
        }

        chain.doFilter(request, response);
    }
}
