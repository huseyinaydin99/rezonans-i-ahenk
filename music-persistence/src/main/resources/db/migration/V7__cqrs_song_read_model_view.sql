-- CQRS Read Model: Materialized/Optimized Read View
-- Decouples query reads from write transactional tables to prevent locking and dirty-checking overhead.
CREATE OR REPLACE VIEW v_song_catalog AS
SELECT 
    s.id AS id,
    s.title AS title,
    s.cover_image_url AS cover_image_url,
    s.audio_url AS audio_url,
    s.duration_seconds AS duration_seconds,
    s.play_count AS play_count,
    s.tier AS tier,
    s.release_date AS release_date,
    s.artist_id AS artist_id,
    a.name AS artist_name,
    s.genre_id AS genre_id,
    g.name AS genre_name,
    s.created_date AS created_date
FROM songs s
LEFT JOIN artists a ON s.artist_id = a.id
LEFT JOIN genres g ON s.genre_id = g.id
WHERE s.deleted_date IS NULL;
