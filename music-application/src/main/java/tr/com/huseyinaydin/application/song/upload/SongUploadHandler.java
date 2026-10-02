package tr.com.huseyinaydin.application.song.upload;

import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public abstract class SongUploadHandler {
    private SongUploadHandler nextHandler;

    public SongUploadHandler setNext(SongUploadHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler; // Zincir (Chain) akıcılığı (Fluent) için
    }

    public void handle(SongUploadRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}
