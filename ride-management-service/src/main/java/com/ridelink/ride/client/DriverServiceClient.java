package com.ridelink.ride.client;

import com.ridelink.ride.dto.DriverDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DriverServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DriverServiceClient.class);
    private final RestClient restClient;

    public DriverServiceClient(@Value("${services.driver-service.url:http://localhost:8082}") String driverServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(driverServiceUrl)
                .build();
    }

    public List<DriverDto> getAvailableDrivers(String serviceArea) {
        try {
            String uri = (serviceArea != null && !serviceArea.isBlank())
                    ? "/api/drivers/available?serviceArea=" + serviceArea
                    : "/api/drivers/available";

            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DriverDto>>() {});
        } catch (Exception ex) {
            log.warn("Error communicating with Driver Service: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public void updateDriverAvailability(Long driverId, String status) {
        try {
            restClient.patch()
                    .uri("/api/drivers/{id}/availability", driverId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", status))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Failed to update availability for driver {}: {}", driverId, ex.getMessage());
        }
    }
}
