package com.harmony.playlist_service.controller;

import com.harmony.playlist_service.dto.PlaylistRequest;
import com.harmony.playlist_service.entity.Playlist;
import com.harmony.playlist_service.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist")
@Tag(name = "Playlists", description = "Manage user-created playlists. " +
        "Create playlists, browse existing ones, and curate them by adding songs from the Music Search API.")
public class PlaylistController {

    private final PlaylistService service;

    public PlaylistController(PlaylistService playlistService) {
        this.service = playlistService;
     }

    @Operation(
            summary = "Create a new playlist",
            description = """
                    Creates a new playlist with the given name and an optional initial set of song IDs.
                    The playlist is persisted in the H2 database and can be retrieved or modified later.

                    **Request Body Example:**
                    ```json
                    {
                      "name": "Road Trip Vibes",
                      "songIds": ["dQw4w9WgXcQ", "9bZkp7q19f0"]
                    }
                    ```
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Playlist created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body (missing playlist name)",
                    content = @Content)
    })
     @PostMapping
    public Playlist create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Playlist creation payload with name and optional song IDs",
                    required = true)
            @RequestBody PlaylistRequest request){
        return service.createPlaylist(request);
     }

    @Operation(
            summary = "Get all playlists",
            description = "Retrieves a list of all playlists stored in the system. " +
                    "Each playlist includes its ID, name, and the list of song IDs it contains."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all playlists",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Playlist.class))))
    })
     @GetMapping
    public List<Playlist> getAll(){
        return service.getAllPlaylists();
     }

    @Operation(
            summary = "Get a playlist by ID",
            description = "Retrieves a single playlist by its unique numeric ID. " +
                    "Returns the full playlist object including its name and all associated song IDs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Playlist found and returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class))),
            @ApiResponse(responseCode = "404", description = "Playlist with the given ID was not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public Playlist getPlaylist(
            @Parameter(description = "Unique numeric ID of the playlist", required = true, example = "1")
            @PathVariable Long id) {
        return service.getPlaylist(id);
    }

    @Operation(
            summary = "Add a song to a playlist",
            description = """
                    Appends a song (identified by its YouTube video ID) to an existing playlist.
                    The song ID is added to the playlist's song list and the updated playlist is returned.

                    **Tip:** Use the Music Search API (`GET /music/search`) to discover song IDs,
                    then add them to playlists using this endpoint.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song added successfully; updated playlist returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class))),
            @ApiResponse(responseCode = "404", description = "Playlist with the given ID was not found",
                    content = @Content)
    })
    @PostMapping("/{id}/songs/{songId}")
    public Playlist addSongToPlaylist(
            @Parameter(description = "Unique numeric ID of the playlist", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "YouTube video ID of the song to add", required = true, example = "dQw4w9WgXcQ")
            @PathVariable String songId) {
        return service.addSongToPlaylist(id, songId);
    }
}