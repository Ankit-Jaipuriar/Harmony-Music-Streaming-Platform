package com.harmony.playlist_service.config;

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
    public OpenAPI playlistOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harmony — Playlist API")
                        .version("1.0.0")
                        .description("""
                                The Playlist microservice manages user-created playlists in Harmony.
                                It provides full CRUD operations for playlists and supports adding
                                songs by their video IDs. Data is persisted in an H2 in-memory database.

                                **Key Features:**
                                - Create, retrieve, and manage playlists
                                - Add songs to existing playlists by video ID
                                - JPA-backed persistence with H2 database
                                - RESTful API design with JSON request/response
                                """)
                        .contact(new Contact()
                                .name("Harmony Engineering")
                                .email("engineering@harmony.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local Development"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")));
    }
}
