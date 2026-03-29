package com.harmony.streaming_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents the audio stream information for a requested song")
public class StreamResponse {

    @Schema(description = "The YouTube video ID that was resolved", example = "dQw4w9WgXcQ")
    private String videoId;

    @Schema(description = "Direct audio stream URL for playback in the frontend player",
            example = "https://rr3---sn-example.googlevideo.com/videoplayback?expire=...")
    private String streamUrl;

    public StreamResponse() {
    }

    public StreamResponse(String videoId, String streamUrl) {
        this.videoId = videoId;
        this.streamUrl = streamUrl;
    }
}
