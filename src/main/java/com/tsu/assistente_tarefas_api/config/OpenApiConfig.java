package com.tsu.assistente_tarefas_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Assistente de Tarefas API")
                        .version("v1")
                        .description("API REST para gerenciamento de tarefas com auxílio de Inteligência Artificial.")
                        .contact(new Contact()
                                .name("Gabriel Tsuyoshi Sato")
                                .email("gabrieltsusato@gmail.com")));
    }
}