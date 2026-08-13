package com.ai.gateway_service.security;

import com.ai.gateway_service.model.ApiKey;
import com.ai.gateway_service.repository.ApiKeyRepository;
import com.ai.gateway_service.util.ApiKeyHasher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyHasher apiKeyHasher;

    public ApiKeyAuthFilter(ApiKeyRepository apiKeyRepository, ApiKeyHasher apiKeyHasher) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyHasher = apiKeyHasher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI(); // path = /admin.. or /v1..

        if(path.startsWith("/admin")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"missing_api_key\",\"message\":\"Authorization: Bearer <key> header required\"}");
            return;
        }

        String rawKey = authHeader.substring(7);
        String hashedKey = apiKeyHasher.hash(rawKey);

        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByHashedKeyAndActiveTrue(hashedKey);

        if (apiKeyOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"invalid_api_key\",\"message\":\"API key is invalid or inactive\"}");
            return;
        }
        ApiKey apiKey = apiKeyOpt.get();

        // Attach the verified client identity to the request so downstream code (controller) can use it
        request.setAttribute("clientId", apiKey.getClientName());
        request.setAttribute("rateLimitCapacity", apiKey.getRateLimitCapacity());

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(apiKey.getClientName(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
