package com.harmony.streaming_service.controller;

import com.harmony.streaming_service.dto.StreamResponse;
import com.harmony.streaming_service.service.StreamingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stream")
@Tag(name = "Streaming", description = "Stream audio content from YouTube videos. " +
        "Resolves video IDs into playable audio URLs and tracks listening transitions for analytics.")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Operation(
            summary = "Get audio stream for a song",
            description = """
                    Resolves a YouTube video ID into a direct audio stream URL that can be used
                    for playback in the frontend player. Optionally accepts a `previousVideoId`
                    parameter to track sequential listening transitions.

                    When `previousVideoId` is provided, a Kafka event is published to the
                    `song-play-events` topic, enabling the Recommendation service to build
                    its transition-frequency graph for "Next Song" predictions.

                    **Usage Examples:**
                    - First song:  `GET /stream/dQw4w9WgXcQ`
                    - Transition:  `GET /stream/9bZkp7q19f0?previousVideoId=dQw4w9WgXcQ`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stream URL resolved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StreamResponse.class))),
            @ApiResponse(responseCode = "404", description = "Video ID not found or unavailable",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to resolve stream from YouTube",
                    content = @Content)
    })
    @GetMapping("/{videoId}")
    public StreamResponse getStream(
            @Parameter(description = "YouTube video ID of the song to stream",
                    required = true, example = "dQw4w9WgXcQ")
            @PathVariable String videoId,
            @Parameter(description = "Video ID of the previously played song (used for transition tracking). " +
                    "When provided, a Kafka event is emitted for the recommendation engine.",
                    required = false, example = "9bZkp7q19f0")
            @RequestParam(required = false) String previousVideoId) {
        return streamingService.getStream(videoId, previousVideoId);
    }
}