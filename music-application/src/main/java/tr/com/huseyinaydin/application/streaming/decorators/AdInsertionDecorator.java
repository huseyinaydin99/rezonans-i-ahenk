package tr.com.huseyinaydin.application.streaming.decorators;

import tr.com.huseyinaydin.application.streaming.IAudioStream;

public class AdInsertionDecorator extends AudioStreamDecorator {

    public AdInsertionDecorator(IAudioStream wrappedStream) {
        super(wrappedStream);
    }

    @Override
    public String play() {
        String adMessage = "[REKLAM] Premium'a geçin, müziğiniz hiç bölünmesin!\n";
        return adMessage + super.play(); // Ham sesin başına reklamı ekler (Sarmalar)
    }
}
