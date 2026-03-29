package com.harmony.music_search_service.service;

import com.harmony.music_search_service.dto.SongResponse;

import java.util.List;

public interface MusicSearchService{

    List<SongResponse> searchSongs(String query);
}