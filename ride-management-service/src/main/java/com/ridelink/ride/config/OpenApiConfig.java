package com.ridelink.ride.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rideServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink - Ride Management Service API")
                        .description("Microservice for managing ride requests, driver assignment, ride lifecycle state machine, and trip history. Owned by Munasinghe D.D.T (IT24102566).")
                        .version("1.0.0")
                        .contact(new Contact().name("Munasinghe D.D.T").email("it24102566@my.sliit.lk"))
                        .license(new License().name("Educational Use - IT3130")));
    }
}
