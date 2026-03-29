package com.harmony.music_search_service.controller;


import com.harmony.music_search_service.dto.SongResponse;
import com.harmony.music_search_service.service.MusicSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/music")
@Tag(name = "Music Search", description = "Search for songs across YouTube's music catalog. " +
        "Returns structured metadata including titles, uploaders, and thumbnail artwork.")
public  class MusicSearchController {

    private final MusicSearchService musicSearchService;

    public MusicSearchController(MusicSearchService musicSearchService) {
        this.musicSearchService = musicSearchService;
    }

    @Operation(
            summary = "Search for songs",
            description = """
                    Performs a full-text search against YouTube's music library using the provided query string.
                    Returns a list of matching songs with metadata including the video ID, title,
                    uploader/artist name, and a URL to the song's thumbnail image.

                    **Usage Example:**
                    `GET /music/search?q=Bohemian Rhapsody`

                    The results can be used directly with the Streaming API to play a song,
                    or with the Playlist API to save songs to a playlist.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SongResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Missing or empty search query parameter",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "YouTube API error or internal service failure",
                    content = @Content)
    })
    @GetMapping("/search")
    public List<SongResponse> searchSongs(
            @Parameter(description = "The search query string (song title, artist, or keywords)",
                    required = true,
                    example = "Shape of You Ed Sheeran")
            @RequestParam String q){
        return musicSearchService.searchSongs(q);
    }

}