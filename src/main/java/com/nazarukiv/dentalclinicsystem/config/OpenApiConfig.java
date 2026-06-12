package com.nazarukiv.dentalclinicsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dentalClinicOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dental Clinic System API")
                        .description("REST API for managing patients, dentists and appointments.")
                        .version("1.0"));
    }
}
