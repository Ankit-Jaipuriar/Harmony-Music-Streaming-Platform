package com.harmony.recommendation_service.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final Map<String, Integer> songPlayCounts = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> nextSongTransitions = new ConcurrentHashMap<>();

    public void recordPlay(String videoId, String previousVideoId) {
        songPlayCounts.put(videoId, songPlayCounts.getOrDefault(videoId, 0) + 1);
        
        if (previousVideoId != null && !previousVideoId.isEmpty()) {
            Map<String, Integer> transitions = nextSongTransitions.computeIfAbsent(previousVideoId, k -> new ConcurrentHashMap<>());
            transitions.put(videoId, transitions.getOrDefault(videoId, 0) + 1);
        }
    }

    public List<String> getTopRecommendations(int limit) {
        return songPlayCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public String getNextRecommendation(String currentVideoId) {
        Map<String, Integer> transitions = nextSongTransitions.get(currentVideoId);
        
        if (transitions == null || transitions.isEmpty()) {
            // Fallback to top recommendation if no transitions found
            List<String> top = getTopRecommendations(5);
            if(top.isEmpty()) return null;
            // Return a random one from top to avoid loops
            return top.get((int)(Math.random() * top.size()));
        }

        return transitions.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}

