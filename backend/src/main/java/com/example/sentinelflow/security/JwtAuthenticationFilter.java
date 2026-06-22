package com.example.sentinelflow.security;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final Logger jwtAuthLogger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Estrai il token dall'header Authorization
            String authHeader = request.getHeader("Authorization");
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);  // Toglie "Bearer "
            }

            // 2. Se il token esiste, validalo
            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                jwtAuthLogger.info("Token validated for user: {}", username);

                // 3. Crea un UsernamePasswordAuthenticationToken
                UsernamePasswordAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                                username, // principal (l'utente)
                                null, // credentials (null)
                                new ArrayList<>() // authorities (permessi, vuoto per ora)
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // 4. Continua con la prossima richiesta
            filterChain.doFilter(request, response);

        } catch (JwtException | ServletException | IOException e) {
            jwtAuthLogger.error("JWT filter error: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
}
