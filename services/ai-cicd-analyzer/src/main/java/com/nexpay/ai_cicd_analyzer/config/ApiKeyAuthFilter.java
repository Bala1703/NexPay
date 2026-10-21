
package com.nexpay.ai_cicd_analyzer.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    private final String configuredApiKey;

    public ApiKeyAuthFilter(String configuredApiKey) {
        this.configuredApiKey = configuredApiKey;

        // Diagnostic: print whether a key is configured, never the key itself.
        System.out.println(
                "API key configured: "
                        + (configuredApiKey != null
                        && !configuredApiKey.isBlank())
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/actuator/health")
                || path.startsWith("/actuator/health/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (configuredApiKey == null || configuredApiKey.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "API key authentication is not configured"
            );
            return;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (providedApiKey == null
                || !MessageDigest.isEqual(
                        configuredApiKey.getBytes(StandardCharsets.UTF_8),
                        providedApiKey.getBytes(StandardCharsets.UTF_8))) {

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid API key"
            );
            return;
        }

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "nexpay-api-client",
                        null,
                        AuthorityUtils.createAuthorityList("ROLE_API_CLIENT")
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
