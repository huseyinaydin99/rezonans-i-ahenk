package tr.com.huseyinaydin.infrastructure;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface IElasticSongRepository extends ElasticsearchRepository<ElasticSong, String> {
    List<ElasticSong> findByTitleContainingOrArtistContaining(String title, String artist);
}
