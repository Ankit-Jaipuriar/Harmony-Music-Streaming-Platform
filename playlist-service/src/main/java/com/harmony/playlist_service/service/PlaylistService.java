package com.harmony.playlist_service.service;

import com.harmony.playlist_service.dto.PlaylistRequest;
import com.harmony.playlist_service.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    Playlist createPlaylist(PlaylistRequest request);
    List<Playlist> getAllPlaylists();
    Playlist addSongToPlaylist(Long id, String songId);
    Playlist getPlaylist(Long id);
}