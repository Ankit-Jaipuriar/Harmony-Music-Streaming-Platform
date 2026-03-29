CREATE TABLE IF NOT EXISTS playlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS playlist_songs_ids (
    playlist_id BIGINT NOT NULL,
    songs_ids VARCHAR(255)
);
