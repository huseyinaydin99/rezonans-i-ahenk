package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;

public interface IItunesService {
    ItunesResponseDto searchMusic(String term, int limit);
}
