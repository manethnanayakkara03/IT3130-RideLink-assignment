package com.ridelink.driver.client;

import com.ridelink.driver.dto.AccountVerificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AccountServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClient.class);
    private final RestClient restClient;

    public AccountServiceClient(@Value("${services.account-service.url:http://localhost:8081}") String accountServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }

    public AccountVerificationDto verifyAccount(Long accountId) {
        try {
            return restClient.get()
                    .uri("/api/accounts/{id}/exists", accountId)
                    .retrieve()
                    .body(AccountVerificationDto.class);
        } catch (Exception ex) {
            log.warn("Account Service communication error for accountId {}: {}", accountId, ex.getMessage());
            // Fallback: If Account Service is unreachable during standalone test or dev, return null
            return null;
        }
    }
}
