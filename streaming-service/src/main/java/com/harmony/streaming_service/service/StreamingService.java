package com.harmony.streaming_service.service;

import com.harmony.streaming_service.dto.StreamResponse;

public interface StreamingService {

    StreamResponse getStream(String videoId, String previousVideoId);
}