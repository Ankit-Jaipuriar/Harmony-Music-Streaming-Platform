package com.harmony.playlist_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request payload for creating a new playlist")
public class PlaylistRequest {

    @Schema(description = "Name of the playlist", example = "Chill Vibes", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "List of YouTube video IDs to include in the playlist",
            example = "[\"dQw4w9WgXcQ\", \"9bZkp7q19f0\"]",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> songIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }
}