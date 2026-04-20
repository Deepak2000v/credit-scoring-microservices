package com.spring.cloudgateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * GatewaySecurityConfig
 *
 * This is the security brain of the entire system.
 * All requests from the Angular UI hit this gateway first.
 *
 * What it does:
 * 1. Allows /login and /register through WITHOUT a token (public endpoints)
 * 2. For ALL other routes (/users/**, /score/**, /data/**),
 *    it validates the JWT token before forwarding to microservices
 * 3. If token is missing or invalid → returns 401 Unauthorized immediately
 * 4. If token is valid → forwards request to the correct microservice
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Security filter chain using reactive WebFlux (required for Spring Cloud Gateway).
     * Note: We use ServerHttpSecurity (not HttpSecurity) because Gateway is WebFlux-based.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints — no token required
                .pathMatchers("/login", "/register").permitAll()
                // UserManagementMS routes — must be authenticated
                .pathMatchers("/users/**").authenticated()
                // CreditScoringServiceMS routes — must be authenticated
                .pathMatchers("/credit/**").authenticated()
                // Everything else — permit (adjust as needed)
                .anyExchange().permitAll()
            )
            // Add our custom JWT filter BEFORE the default security filter
            .addFilterBefore(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
            // Enable OAuth2 login (for Google OAuth flow)
            //.oauth2Login(oauth -> {});

        return http.build();
    }

    /**
     * Custom reactive WebFilter that validates the JWT Bearer token.
     * Runs on every request except /login and /register.
     *
     * Flow:
     * 1. Check if path is public (/login, /register) → skip validation
     * 2. Read the Authorization header
     * 3. Strip "Bearer " prefix and verify the JWT signature
     * 4. If valid → pass the request through to routing
     * 5. If invalid/missing → return 401 immediately
     */
//    @Bean
//    public WebFilter jwtValidationFilter() {
//        return (ServerWebExchange exchange, WebFilterChain chain) -> {
//            String path = exchange.getRequest().getURI().getPath();
//
//            // Skip JWT check for public endpoints
//            if (path.equals("/login") || path.equals("/register") || path.startsWith("/oauth2")) {
//                return chain.filter(exchange);
//            }
//
//            // Read Authorization header
//            String authHeader = exchange.getRequest()
//                    .getHeaders()
//                    .getFirst(HttpHeaders.AUTHORIZATION);
//
//            // If no Bearer token → reject with 401
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//
//            // Extract and verify the JWT token
//            String token = authHeader.substring(7);
//            try {
//                JWT.require(Algorithm.HMAC256(jwtSecret))
//                        .build()
//                        .verify(token);
//                // Token valid → continue to microservice routing
//                return chain.filter(exchange);
//            } catch (JWTVerificationException e) {
//                // Token invalid or expired → reject with 401
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        };
//    }
    @Bean
    public WebFilter jwtValidationFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // Skip public endpoints
            if (path.equals("/login") || path.equals("/register") || path.startsWith("/oauth2")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                var decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                        .build()
                        .verify(token);

                // 🔥 IMPORTANT PART: set authentication
                String username = decodedJWT.getSubject();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, List.of());

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

            } catch (JWTVerificationException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}
