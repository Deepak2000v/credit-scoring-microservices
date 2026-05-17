package com.ms.credit.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserManagementClient {

    private static final Logger logger = LogManager.getLogger(UserManagementClient.class);

    // URL endpoint for the User Management microservice
    private static final String USER_SERVICE_URL = "/users/";

    @Autowired
    private WebClient webClient;

    /**
     * Retrieves user email from UserManagementMS using a reactive WebClient GET call.
     * Called by CreditScoreService when we need the user's email for notifications.
     *
     * @param userId The ID of the user whose details are to be fetched
     * @return A Mono that emits the user's email as a String, or an error signal
     */
    public Mono<String> getUserDetails(int userId) {
        logger.info("Calling UserManagementMS for userId: {}", userId);

        return webClient.get()
                .uri(USER_SERVICE_URL + "{userId}", userId)   // Append userId to URL
                .retrieve()                                    // Extract the response
                .bodyToMono(String.class);                     // Convert body to Mono<String>
    }
}
