package com.yappyd.authservice.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API сервиса авторизации")
                        .version("0.0.1-SNAPSHOT")
                        .description("Документация API для сервиса авторизации")
                )
                .addServersItem(new Server().url("http://localhost:8081").description("localhost"));
    }
}
