package tr.com.huseyinaydin.application.streaming.decorators;

import tr.com.huseyinaydin.application.streaming.IAudioStream;

/**
 * Diğer tüm özellik ekleyicilerin (Filtre, Reklam) türeyeceği soyut Dekoratör Sınıfı
 */
public abstract class AudioStreamDecorator implements IAudioStream {
    protected final IAudioStream wrappedStream;

    public AudioStreamDecorator(IAudioStream wrappedStream) {
        this.wrappedStream = wrappedStream;
    }

    @Override
    public String play() {
        return wrappedStream.play();
    }
}
