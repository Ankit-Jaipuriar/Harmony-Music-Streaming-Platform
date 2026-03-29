package com.harmony.playlist_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Schema(description = "A user-created playlist containing a curated collection of songs")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated unique playlist ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "User-defined name for the playlist", example = "Morning Motivation")
    private String name;

    @ElementCollection
    @Schema(description = "Ordered list of YouTube video IDs in this playlist",
            example = "[\"dQw4w9WgXcQ\", \"9bZkp7q19f0\"]")
    private List<String> songsIds;

    public Playlist(String name, List<String> songsIds) {
        this.name = name;
        this.songsIds = songsIds;
    }

    public Playlist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSongsIds() {
        return songsIds;
    }

    public void setSongsIds(List<String> songsIds) {
        this.songsIds = songsIds;
    }
}