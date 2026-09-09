package com.yahya.erphrapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// setting up the OpenApi documentation
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI erpHrOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP HR & Payroll API")
                        .version("1.0")
                        .description("API for managing employees, payroll, loans, leaves, and attendance"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
