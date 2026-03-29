package com.harmony.playlist_service.service.impl;

import com.harmony.playlist_service.dto.PlaylistRequest;
import com.harmony.playlist_service.entity.Playlist;
import com.harmony.playlist_service.repository.PlaylistRepository;
import com.harmony.playlist_service.service.PlaylistService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class    PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository repository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
        this.repository = playlistRepository;
    }

    @Override
    public Playlist createPlaylist(PlaylistRequest request){
        Playlist playlist = new Playlist(
                request.getName(),
                request.getSongIds()
        );
        return repository.save(playlist);
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        return repository.findAll();
    }

    @Override
    public Playlist addSongToPlaylist(Long id, String songId) {
        Playlist playlist = repository.findById(id).orElseThrow();
        if(playlist.getSongsIds() == null) {
            playlist.setSongsIds(new java.util.ArrayList<>());
        }
        if(!playlist.getSongsIds().contains(songId)) {
            playlist.getSongsIds().add(songId);
        }
        return repository.save(playlist);
    }
    
    @Override
    public Playlist getPlaylist(Long id) {
        return repository.findById(id).orElseThrow();
    }
}