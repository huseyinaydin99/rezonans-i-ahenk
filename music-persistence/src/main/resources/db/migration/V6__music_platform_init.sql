-- Artists Table
CREATE TABLE artists (
    id RAW(16) NOT NULL PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    image_url VARCHAR2(1000),
    bio CLOB,
    monthly_listeners NUMBER(19, 0) DEFAULT 0 NOT NULL,
    is_verified NUMBER(1, 0) DEFAULT 0 NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    deleted_date TIMESTAMP
);

-- Genres Table
CREATE TABLE genres (
    id RAW(16) NOT NULL PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    image_url VARCHAR2(1000),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    deleted_date TIMESTAMP
);

-- Songs Table
CREATE TABLE songs (
    id RAW(16) NOT NULL PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    cover_image_url VARCHAR2(1000),
    audio_url VARCHAR2(1000) NOT NULL,
    duration_seconds NUMBER(19, 0) NOT NULL,
    play_count NUMBER(19, 0) DEFAULT 0 NOT NULL,
    tier VARCHAR2(50) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    artist_id RAW(16) NOT NULL,
    genre_id RAW(16) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    deleted_date TIMESTAMP,
    CONSTRAINT fk_song_artist FOREIGN KEY (artist_id) REFERENCES artists(id),
    CONSTRAINT fk_song_genre FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- Playlists Table
CREATE TABLE playlists (
    id RAW(16) NOT NULL PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    user_id RAW(16) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    deleted_date TIMESTAMP
);

-- Playlist Songs Table (Many-to-Many logic)
CREATE TABLE playlist_songs (
    id RAW(16) NOT NULL PRIMARY KEY,
    playlist_id RAW(16) NOT NULL,
    song_id RAW(16) NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    deleted_date TIMESTAMP,
    CONSTRAINT fk_ps_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id),
    CONSTRAINT fk_ps_song FOREIGN KEY (song_id) REFERENCES songs(id)
);

