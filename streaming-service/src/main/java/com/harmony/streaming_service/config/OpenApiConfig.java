package com.harmony.streaming_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI streamingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harmony — Streaming API")
                        .version("1.0.0")
                        .description("""
                                The Streaming microservice is the core playback engine of Harmony.
                                It resolves YouTube video IDs into streamable audio URLs and
                                publishes listening activity events to Kafka for downstream analytics.

                                **Key Features:**
                                - Resolves video IDs to direct audio stream URLs
                                - Publishes song-play transition events via Kafka
                                - Tracks sequential listening patterns for recommendation engine
                                """)
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Development"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")));
    }
}
