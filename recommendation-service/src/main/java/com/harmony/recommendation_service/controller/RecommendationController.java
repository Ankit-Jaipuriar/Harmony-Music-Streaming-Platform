package com.harmony.recommendation_service.controller;

import com.harmony.recommendation_service.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
@Tag(name = "Recommendations", description = "AI-powered song recommendation engine. " +
        "Provides \"Top Songs\" leaderboards and predictive \"Next Song\" suggestions " +
        "based on real-time listening patterns consumed from Kafka.")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(
            summary = "Get top recommended songs",
            description = """
                    Returns a ranked leaderboard of the most-played songs across all users.
                    Song play counts are aggregated in real-time from Kafka `song-play-events`.
                    The list is sorted by descending popularity (play count).

                    **Use cases:**
                    - Populate a "Trending Now" section on the homepage
                    - Discover popular music across the platform
                    - Build curated playlists from top recommendations
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top recommended song IDs",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class,
                                    description = "YouTube video ID", example = "dQw4w9WgXcQ")))),
            @ApiResponse(responseCode = "500", description = "Internal service error",
                    content = @Content)
    })
    @GetMapping("/top")
    public List<String> getTopRecommendations(
            @Parameter(description = "Maximum number of songs to return (default: 10, max: 100)",
                    required = false, example = "10",
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100"))
            @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.getTopRecommendations(limit);
    }

    @Operation(
            summary = "Get next song recommendation",
            description = """
                    Predicts the most likely next song based on the currently playing song.
                    Uses a transition-frequency graph built from real-time Kafka listening events.
                    The algorithm identifies which song is most commonly played after the given video ID.

                    **How it works:**
                    1. The Streaming service emits `SongPlayEvent` to Kafka on each song transition
                    2. This service consumes events and builds a weighted graph of transitions
                    3. For any given `currentVideoId`, it returns the highest-frequency successor

                    Returns an empty string if no transition data exists for the given video.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Next song recommendation returned (video ID string)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "9bZkp7q19f0",
                                    description = "YouTube video ID of the predicted next song"))),
            @ApiResponse(responseCode = "500", description = "Internal service error",
                    content = @Content)
    })
    @GetMapping("/next/{currentVideoId}")
    public String getNextRecommendation(
            @Parameter(description = "YouTube video ID of the song currently being played",
                    required = true, example = "dQw4w9WgXcQ")
            @PathVariable String currentVideoId) {
        return recommendationService.getNextRecommendation(currentVideoId);
    }
}
