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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

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
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeExchange(exchanges -> exchanges
//                // Public endpoints — no token required
//                .pathMatchers("/login", "/register").permitAll()
//                // UserManagementMS routes — must be authenticated
//                .pathMatchers("/users/**").authenticated()
//                // CreditScoringServiceMS routes — must be authenticated
//                .pathMatchers("/credit/**").authenticated()
//                // Everything else — permit (adjust as needed)
//                .anyExchange().permitAll()
//            )
//            // Add our custom JWT filter BEFORE the default security filter
//            .addFilterBefore(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
//            // Enable OAuth2 login (for Google OAuth flow)
//            //.oauth2Login(oauth -> {});
//
//        return http.build();
//    }
    //gemini
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                // 1. TELL SECURITY TO USE YOUR CORS BEAN
//                .cors(cors -> cors.configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowedOrigins(List.of("http://localhost:4200"));
//                    config.setAllowedMethods(List.of("*"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowCredentials(true);
//                    return config;
//                }))
//                .csrf(csrf -> csrf.disable())
//                .authorizeExchange(exchanges -> exchanges
//                        // 2. ALWAYS PERMIT OPTIONS (PREFLIGHT)
//                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
//                        .pathMatchers("/login", "/register").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .addFilterBefore(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
//
//        return http.build();
//    }

    //gpt working
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//
//        return http
//                // ❌ Disable default CORS (we use CorsWebFilter instead)
//                .cors(cors -> cors.disable())
//
//                // Disable CSRF for APIs
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeExchange(exchanges -> exchanges
//
//                        // ✅ VERY IMPORTANT: allow preflight requests
//                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
//
//                        .pathMatchers("/actuator/**").permitAll()
//
//                        // Public APIs
//                        .pathMatchers("/login", "/register").permitAll()
//
//                        // Everything else requires authentication
//                        .anyExchange().authenticated()
//                )
//
//                // Add JWT filter
//                .addFilterBefore(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
//
//                .build();
//    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http

                // Disable default Spring Security CORS
                .cors(cors -> {})

                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchanges -> exchanges

                        // Allow preflight CORS requests
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .pathMatchers("/login", "/register").permitAll()

                        // Health endpoints
                        .pathMatchers("/actuator/**").permitAll()

                        // Everything else secured
                        .anyExchange().authenticated()
                )

                // Add JWT filter
                .addFilterBefore(jwtValidationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
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

    //working
//    @Bean
//    public WebFilter jwtValidationFilter() {
//        return (ServerWebExchange exchange, WebFilterChain chain) -> {
//
//            String path = exchange.getRequest().getURI().getPath();
//
//            // Skip public endpoints
//            if (path.equals("/login") || path.equals("/register") || path.startsWith("/oauth2")) {
//                return chain.filter(exchange);
//            }
//
//            String authHeader = exchange.getRequest()
//                    .getHeaders()
//                    .getFirst(HttpHeaders.AUTHORIZATION);
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//
//            String token = authHeader.substring(7);
//
//            try {
//                var decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
//                        .build()
//                        .verify(token);
//
//                // 🔥 IMPORTANT PART: set authentication
//                String username = decodedJWT.getSubject();
//
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(username, null, List.of());
//
//                return chain.filter(exchange)
//                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
//
//            } catch (JWTVerificationException e) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        };
//    }

    //new
    @Bean
    public WebFilter jwtValidationFilter() {

        return (ServerWebExchange exchange, WebFilterChain chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // ✅ VERY IMPORTANT: allow CORS preflight requests
            if (exchange.getRequest().getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            // ✅ Skip public endpoints
            if (path.equals("/login")
                    || path.equals("/register")
                    || path.startsWith("/oauth2")) {

                return chain.filter(exchange);
            }

            // ✅ Read Authorization header
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // ❌ No Bearer token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // ✅ Extract token
            String token = authHeader.substring(7);

            try {

                // ✅ Verify JWT
                var decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                        .build()
                        .verify(token);

                // ✅ Extract username
                String username = decodedJWT.getSubject();

                // ✅ Create authentication object
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of()
                        );

                // ✅ Store authentication in security context
                return chain.filter(exchange)
                        .contextWrite(
                                ReactiveSecurityContextHolder.withAuthentication(auth)
                        );

            } catch (JWTVerificationException e) {

                // ❌ Invalid token
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
