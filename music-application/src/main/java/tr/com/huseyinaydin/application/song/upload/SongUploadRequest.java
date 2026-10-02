package tr.com.huseyinaydin.application.song.upload;

public class SongUploadRequest {
    private final String fileName;
    private final String format;
    private final byte[] fileData;
    private final String uploaderId;

    public SongUploadRequest(String fileName, String format, byte[] fileData, String uploaderId) {
        this.fileName = fileName;
        this.format = format;
        this.fileData = fileData;
        this.uploaderId = uploaderId;
    }

    public String getFileName() { return fileName; }
    public String getFormat() { return format; }
    public byte[] getFileData() { return fileData; }
    public String getUploaderId() { return uploaderId; }
}
