package com.harmony.streaming_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kafka event representing a song play transition, used for building recommendation graphs")
public class SongPlayEvent {

    @Schema(description = "YouTube video ID of the song that was just played", example = "dQw4w9WgXcQ")
    private String videoId;

    @Schema(description = "YouTube video ID of the song that was played immediately before this one",
            example = "9bZkp7q19f0", nullable = true)
    private String previousVideoId;
}
