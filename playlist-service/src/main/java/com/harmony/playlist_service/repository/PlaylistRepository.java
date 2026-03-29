package com.harmony.playlist_service.repository;

import com.harmony.playlist_service.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

}