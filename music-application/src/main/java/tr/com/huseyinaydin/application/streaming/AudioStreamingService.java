package tr.com.huseyinaydin.application.streaming;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.streaming.decorators.AdInsertionDecorator;
import tr.com.huseyinaydin.application.streaming.decorators.HighResEqualizerDecorator;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.domain.user.AppUser;
import tr.com.huseyinaydin.domain.enums.MembershipTier;

@Service
public class AudioStreamingService {

    /**
     * Kullanıcının abonelik modeline göre ham sesin üzerine dinamik (Runtime)
     * olarak yeni özellikler (Reklam, EQ) giydirir.
     */
    public String startStream(AppUser user, Song song) {
        // 1. Önce en içteki saf nesneyi (Ham Ses) oluştur.
        IAudioStream stream = new BaseAudioStream(song);

        // 2. Eğer kullanıcı Basic ise, saf sesin üzerini "Reklam" kağıdıyla sar (Decorate)
        if (user.getTier() == MembershipTier.BASIC) {
            stream = new AdInsertionDecorator(stream);
        }

        // 3. Eğer kullanıcı Elite ise, saf sesin üzerini "Ekolayzer (Hi-Fi)" kağıdıyla sar
        if (user.getTier() == MembershipTier.ELITE) {
            stream = new HighResEqualizerDecorator(stream);
        }

        // İstersek zincirleme (Soğan Zarı gibi) sarabiliriz: 
        // stream = new WatermarkDecorator(new HighResEqualizerDecorator(new BaseAudioStream(song)));

        System.out.println("---- Akış Başlatıldı (" + user.getUsername() + ") ----");
        String finalOutput = stream.play();
        System.out.println(finalOutput);
        return finalOutput;
    }
}
