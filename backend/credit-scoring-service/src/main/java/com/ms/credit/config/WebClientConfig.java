package com.ms.credit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${user.management.service.url}")
    private String userManagementServiceUrl;

    /**
     * WebClient bean configured to call UserManagementMS.
     * Used in UserManagementClient to fetch user email by userId.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(userManagementServiceUrl)
                .build();
    }
}
