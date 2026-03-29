package com.harmony.music_search_service.service.impl;

import com.harmony.music_search_service.dto.SongResponse;
import com.harmony.music_search_service.service.MusicSearchService;
import com.harmony.music_search_service.service.client.YoutubeClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicSearchServiceImpl implements MusicSearchService {

    private final YoutubeClient youtubeClient;

    public MusicSearchServiceImpl(YoutubeClient youtubeClient) {
        this.youtubeClient = youtubeClient;
    }

    @Override
    public List<SongResponse> searchSongs(String query) {
        return youtubeClient.searchSongs(query);
    }
}