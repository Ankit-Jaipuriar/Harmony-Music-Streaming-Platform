package com.harmony.streaming_service.service.impl;

import com.harmony.streaming_service.client.YoutubeStreamClient;
import com.harmony.streaming_service.dto.StreamResponse;
import com.harmony.streaming_service.producer.SongEventProducer;
import com.harmony.streaming_service.service.StreamingService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class StreamingServiceImpl implements StreamingService {

    private final YoutubeStreamClient youtubeStreamClient;
    private final SongEventProducer songEventProducer;

    public StreamingServiceImpl(YoutubeStreamClient youtubeStreamClient, SongEventProducer songEventProducer) {
        this.youtubeStreamClient = youtubeStreamClient;
        this.songEventProducer = songEventProducer;
    }

    @Override
    public StreamResponse getStream(String videoId, String previousVideoId) {
        try {
            String streamUrl = youtubeStreamClient.getStreamUrl(videoId);
            try {
                songEventProducer.sendEvent(videoId, previousVideoId);
            } catch (Exception ex) {
                System.err.println("Failed to send Kafka event: " + ex.getMessage());
            }
            return new StreamResponse(videoId, streamUrl);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Unable to resolve a playable YouTube stream for video " + videoId, e);
        }
    }
}

