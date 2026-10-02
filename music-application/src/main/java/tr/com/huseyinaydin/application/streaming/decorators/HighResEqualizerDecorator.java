package tr.com.huseyinaydin.application.streaming.decorators;

import tr.com.huseyinaydin.application.streaming.IAudioStream;

public class HighResEqualizerDecorator extends AudioStreamDecorator {

    public HighResEqualizerDecorator(IAudioStream wrappedStream) {
        super(wrappedStream);
    }

    @Override
    public String play() {
        String originalStream = super.play();
        // Varsayılan bit hızını değiştirip yerine Hi-Fi eklentisini yapıştırır
        originalStream = originalStream.replace("(Bitrate: 128kbps)", "");
        return originalStream + "\n[FİLTRE] 🎛️ Ekolayzer Aktif (Hi-Fi 320kbps Kayıpsız Ses Kalitesi ile çalınıyor)";
    }
}
