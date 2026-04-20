package com.spring.cloudgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CorsConfig
 *
 * Configures Cross-Origin Resource Sharing (CORS) for the Gateway.
 * This allows the Angular frontend (running on http://localhost:4200)
 * to make API calls to this Gateway (http://localhost:8080).
 *
 * Without this, the browser would block all API requests from Angular
 * due to the Same-Origin Policy.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, Authorization headers)
        config.setAllowCredentials(true);

        // Allow requests from Angular dev server
        config.addAllowedOrigin("http://localhost:4200");

        // Allow all headers (including Authorization, Content-Type, etc.)
        config.addAllowedHeader("*");

        // Allow all HTTP methods
        config.addAllowedMethod("*");

        // Apply this CORS config to all routes ("/**")
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
