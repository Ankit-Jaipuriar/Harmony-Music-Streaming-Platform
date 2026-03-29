package com.harmony.music_search_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI musicSearchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harmony — Music Search API")
                        .version("1.0.0")
                        .description("""
                                The Music Search microservice powers Harmony's song discovery engine.
                                It integrates with YouTube's data API to search for music tracks
                                and returns enriched metadata including titles, uploaders, and thumbnails.

                                **Key Features:**
                                - Full-text song search powered by YouTube
                                - Returns structured metadata for each result
                                - Designed for high-throughput, low-latency queries
                                """)
                        .contact(new Contact()
                                .name("Harmony Engineering")
                                .email("engineering@harmony.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")));
    }
}
