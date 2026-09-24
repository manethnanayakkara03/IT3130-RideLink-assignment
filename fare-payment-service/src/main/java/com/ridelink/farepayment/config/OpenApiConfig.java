package com.ridelink.farepayment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI farePaymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink - Fare & Payment Service API")
                        .description("Microservice for fare estimation, final fare calculation, simulated payments, and receipt generation. Owned by Priyamalka W.D.N (IT24102758).")
                        .version("1.0.0")
                        .contact(new Contact().name("Priyamalka W.D.N").email("it24102758@my.sliit.lk"))
                        .license(new License().name("Educational Use - IT3130")));
    }
}
