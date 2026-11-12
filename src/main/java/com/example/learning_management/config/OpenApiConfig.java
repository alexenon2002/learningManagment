package com.example.learning_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI learningManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Learning Management API")
                        .version("v1")
                        .description("REST API для управления обучением: "
                                + "преподаватели, курсы, студенты, группы и расписание."));
    }
}
