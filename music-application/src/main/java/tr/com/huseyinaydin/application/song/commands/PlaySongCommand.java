package tr.com.huseyinaydin.application.song.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IListeningHistoryRepository;
import tr.com.huseyinaydin.application.ports.IPlaybackService;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.application.song.dtos.PlaySongResult;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.util.Map;

/**
 * Sunucu taraflı şarkı çalma yetki kapısı (Tier Gate) ve dinleme geçmişi kaydını icra eden CQRS komutu.
 */
public record PlaySongCommand(
        Long songId,
        String userEmail,
        String userTier
) implements ICommand<PlaySongResult> {

    private static final Logger log = LoggerFactory.getLogger(PlaySongCommand.class);

    @Component
    public static class Handler implements ICommandHandler<PlaySongCommand, PlaySongResult> {

        private final IPlaybackService playbackService;
        private final ISongSearchService songSearchService;
        private final IListeningHistoryRepository listeningHistoryRepository;
        private final IProfileService profileService;

        public Handler(@Autowired(required = false) IPlaybackService playbackService,
                       @Autowired(required = false) ISongSearchService songSearchService,
                       @Autowired(required = false) IListeningHistoryRepository listeningHistoryRepository,
                       @Autowired(required = false) IProfileService profileService) {
            this.playbackService = playbackService;
            this.songSearchService = songSearchService;
            this.listeningHistoryRepository = listeningHistoryRepository;
            this.profileService = profileService;
        }

        @Override
        public PlaySongResult handle(PlaySongCommand command) {
            log.info("Executing PlaySongCommand for songId: {}, userEmail: {}, tier: {}",
                    command.songId(), command.userEmail(), command.userTier());

            // 1. Şarkı meta verilerini katalog/servisten çöz
            Map<String, Object> song = (songSearchService != null) ? songSearchService.getSongById(command.songId()) : null;
            String title = (song != null && song.get("title") != null) ? String.valueOf(song.get("title")) : ("Şarkı #" + command.songId());
            String artist = (song != null && song.get("artist") != null) ? String.valueOf(song.get("artist")) : "";
            String cover = (song != null && song.get("cover") != null) ? String.valueOf(song.get("cover")) : "";

            // 2. Şarkının gerektirdiği üyelik seviyesini tespit et
            MembershipTier requiredTier = MembershipTier.BASIC;
            if (song != null && song.get("tier") != null) {
                requiredTier = MembershipTier.fromString(String.valueOf(song.get("tier")));
            }
            if (playbackService != null) {
                Integer playbackReqLevel = playbackService.getRequiredTierLevel(command.songId());
                if (playbackReqLevel != null && playbackReqLevel > requiredTier.ordinal()) {
                    requiredTier = switch (playbackReqLevel) {
                        case 1 -> MembershipTier.GOLD;
                        case 2 -> MembershipTier.PREMIUM;
                        case 3 -> MembershipTier.ELIT;
                        default -> MembershipTier.BASIC;
                    };
                }
            }

            // 3. Kullanıcının güncel paket seviyesini çöz (claim veya profil üzerinden en güncel olanı al)
            MembershipTier userTier = MembershipTier.fromString(command.userTier());
            if (profileService != null && command.userEmail() != null) {
                UserProfileDto profile = profileService.getProfile(command.userEmail());
                if (profile != null && profile.getTier() != null) {
                    MembershipTier profTier = MembershipTier.fromString(profile.getTier());
                    if (profTier.ordinal() > userTier.ordinal()) {
                        userTier = profTier;
                    }
                }
            }

            // 4. Yetki Kapısı (Tier Gate Kontrolü)
            if (!userTier.canAccess(requiredTier)) {
                String requiredTierDisplay = formatTier(requiredTier);
                String message = "Bu şarkıyı dinlemek için en az " + requiredTierDisplay + " üyeliğine sahip olmanız gerekmektedir.";
                log.warn("Access denied for songId: {}. User Tier: {}, Required Tier: {}",
                        command.songId(), userTier, requiredTier);
                return PlaySongResult.unauthorized(requiredTierDisplay, message);
            }

            // 5. Yetki Yeterli: PlayCount değerini 1 artır
            long updatedPlayCount = 1;
            if (listeningHistoryRepository != null) {
                updatedPlayCount = listeningHistoryRepository.incrementPlayCount(command.songId());
            }

            // 6. Dinleme geçmişi kaydı (ListeningHistory tablosuna ekle, Kafka/Cassandra tetikle)
            if (listeningHistoryRepository != null && command.userEmail() != null) {
                listeningHistoryRepository.recordListeningHistory(command.userEmail(), command.songId());
            }
            if (playbackService != null && command.userEmail() != null) {
                playbackService.recordListenEvent(command.userEmail(), command.songId());
            }
            if (profileService != null && command.userEmail() != null) {
                UserProfileDto profile = profileService.getProfile(command.userEmail());
                if (profile != null) {
                    profile.setListenCount(profile.getListenCount() + 1);
                }
            }

            // 7. Güvenli ses akışı URL'sini belirle
            String audioUrl = (playbackService != null) ? playbackService.getAudioUrl(command.songId()) : "";
            if (audioUrl == null || audioUrl.isBlank()) {
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
            }

            log.info("PlaySongCommand authorized for songId: {}. PlayCount: {}", command.songId(), updatedPlayCount);
            return PlaySongResult.authorized(command.songId(), title, artist, audioUrl, cover, updatedPlayCount);
        }

        private String formatTier(MembershipTier tier) {
            if (tier == null) return "Basic";
            return switch (tier) {
                case GOLD -> "Gold";
                case PREMIUM -> "Premium";
                case ELIT -> "Elit";
                default -> "Basic";
            };
        }
    }
}
