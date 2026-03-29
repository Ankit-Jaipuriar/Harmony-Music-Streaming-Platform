package com.harmony.streaming_service.producer;

import com.harmony.streaming_service.dto.SongPlayEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SongEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SongEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendEvent(String videoId, String previousVideoId) {
        try {
            SongPlayEvent event = new SongPlayEvent(videoId, previousVideoId);
            kafkaTemplate.send("song-play", event).get();
        } catch (Exception e) {
            throw new RuntimeException("Kafka send failed", e);
        }
    }
}