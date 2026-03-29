package com.harmony.recommendation_service.consumer;

import com.harmony.recommendation_service.dto.SongPlayEvent;
import com.harmony.recommendation_service.service.RecommendationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SongEventConsumer {

    private final RecommendationService recommendationService;

    public SongEventConsumer(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @KafkaListener(topics = "song-play", groupId = "recommendation-group")
    public void consume(SongPlayEvent event) {
        recommendationService.recordPlay(event.getVideoId(), event.getPreviousVideoId());
    }
}
