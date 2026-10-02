package tr.com.huseyinaydin.application.ports;

import java.io.InputStream;

public interface IStorageService {
    String uploadAudioFile(String fileName, InputStream fileStream, long size, String contentType);
    String uploadImageFile(String fileName, InputStream fileStream, long size, String contentType);
    void deleteFile(String fileUrl);
}
