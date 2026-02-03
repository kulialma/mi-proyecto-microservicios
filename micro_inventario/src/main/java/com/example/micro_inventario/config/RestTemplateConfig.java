package com.example.micro_inventario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    // Leemos la API Key desde application.properties
    @Value("${api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Timeout de conexión y lectura
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                // Interceptor para enviar API Key en cada request
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().add("X-API-KEY", apiKey);
                    return execution.execute(request, body);
                })
                .build();
    }
}



