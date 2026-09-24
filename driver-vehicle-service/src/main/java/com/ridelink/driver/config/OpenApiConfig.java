package com.ridelink.driver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driverServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink - Driver & Vehicle Service API")
                        .description("Microservice for managing driver operational profiles, vehicle registration, availability, simulated locations, and finding eligible drivers. Owned by Nanayakkara S.N.M (IT24102468).")
                        .version("1.0.0")
                        .contact(new Contact().name("Nanayakkara S.N.M").email("it24102468@my.sliit.lk"))
                        .license(new License().name("Educational Use - IT3130")));
    }
}
