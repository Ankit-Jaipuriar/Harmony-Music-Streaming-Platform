package com.harmony.recommendation_service.config;

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
    public OpenAPI recommendationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harmony — Recommendation API")
                        .version("1.0.0")
                        .description("""
                                The Recommendation microservice provides intelligent song suggestions
                                powered by real-time listening analytics. It consumes Kafka events
                                from the Streaming service to build a transition-frequency graph and
                                uses it to predict what users are most likely to listen to next.

                                **Key Features:**
                                - Real-time Kafka event consumption for listening pattern analysis
                                - Sequence-based "Next Song" prediction using transition graphs
                                - Global "Top Songs" leaderboard based on aggregate play counts
                                - Fully event-driven architecture with no synchronous dependencies
                                """)
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8084").description("Local Development"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")));
    }
}
