package com.harmony.music_search_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents a song result returned from the music search engine")
public class SongResponse {

    @Schema(description = "Unique YouTube video ID for the song", example = "dQw4w9WgXcQ")
    private String id;

    @Schema(description = "Title of the song or video", example = "Rick Astley - Never Gonna Give You Up")
    private String title;

    @Schema(description = "Name of the channel or artist that uploaded the song", example = "Rick Astley")
    private String uploader;

    @Schema(description = "URL to the song's thumbnail image", example = "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg")
    private String thumbnailUrl;

    public SongResponse(String id, String title, String uploader, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.uploader = uploader;
        this.thumbnailUrl = thumbnailUrl;
    }
}