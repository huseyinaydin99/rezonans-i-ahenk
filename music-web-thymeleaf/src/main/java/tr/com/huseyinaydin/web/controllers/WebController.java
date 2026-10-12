package tr.com.huseyinaydin.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;
import tr.com.huseyinaydin.application.home.queries.GetHomeDashboardQuery;
import tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery;
import tr.com.huseyinaydin.application.genre.queries.GetGenreSongCountsQuery;
import tr.com.huseyinaydin.application.artist.queries.GetFeaturedArtistsQuery;
import tr.com.huseyinaydin.application.artist.queries.GetArtistDetailQuery;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;
import tr.com.huseyinaydin.application.profile.queries.GetUserProfileQuery;
import tr.com.huseyinaydin.application.profile.commands.UpdateProfileInfoCommand;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.application.playlist.queries.GetUserPlaylistsQuery;
import tr.com.huseyinaydin.application.playlist.queries.GetPlaylistByIdQuery;
import tr.com.huseyinaydin.application.playlist.commands.CreatePlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.RemoveSongFromPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.application.song.queries.SearchSongsQuery;
import tr.com.huseyinaydin.application.song.queries.GetSongsByIdsQuery;
import tr.com.huseyinaydin.application.ports.ITokenService;
import tr.com.huseyinaydin.web.security.AuthCookies;
import tr.com.huseyinaydin.web.security.CurrentUser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    private final Mediator mediator;
    private final ITokenService tokenService;
    private final AuthCookies authCookies;

    @Autowired
    public WebController(Mediator mediator,
                         @Autowired(required = false) ITokenService tokenService,
                         @Autowired(required = false) AuthCookies authCookies) {
        this.mediator = mediator;
        this.tokenService = tokenService;
        this.authCookies = authCookies;
    }

    @GetMapping("favicon.ico")
    public org.springframework.http.ResponseEntity<Void> returnNoFavicon() {
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery(email));
        model.addAttribute("topGlobalSongs", dashboardData.getTopGlobalSongs());

        List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : Collections.emptyList());

        return "index";
    }

    @GetMapping("/recommendations")
    public String recommendations(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery(email));
        model.addAttribute("personalizedRecommendations", dashboardData.getPersonalizedRecommendations());

        List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : Collections.emptyList());

        return "fragments/recommendations";
    }

    @GetMapping({"/login", "/login.xhtml"})
    public String login() {
        if (CurrentUser.get().isPresent()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping({"/register", "/register.xhtml"})
    public String register() {
        if (CurrentUser.get().isPresent()) {
            return "redirect:/";
        }
        return "register";
    }

    @GetMapping({"/profile", "/profile.xhtml"})
    public String profile(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        UserProfileDto profile = mediator.query(new GetUserProfileQuery(email));
        model.addAttribute("profile", profile);

        int playlistCount = 0;
        List<PlaylistDetailDto> playlists = mediator.query(new GetUserPlaylistsQuery(email));
        if (playlists != null) {
            playlistCount = playlists.size();
        }
        model.addAttribute("playlistCount", playlistCount);

        int likeCount = 0;
        try {
            List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
            if (likedSongIds != null) {
                likeCount = likedSongIds.size();
            }
        } catch (Exception ignored) {}
        model.addAttribute("likeCount", likeCount);

        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery(email));
        model.addAttribute("recommendations", dashboardData != null ? dashboardData.getPersonalizedRecommendations() : Collections.emptyList());

        return "profile";
    }

    @GetMapping({"/edit-profile", "/edit-profile.xhtml"})
    public String editProfile(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        UserProfileDto profile = mediator.query(new GetUserProfileQuery(email));
        model.addAttribute("profile", profile);
        return "edit-profile";
    }

    @PostMapping({"/edit-profile", "/edit-profile.xhtml"})
    public String updateProfilePost(@RequestParam("email") String newEmail,
                                    @RequestParam(value = "fullName", required = false) String fullName,
                                    @RequestParam(value = "username", required = false) String username,
                                    @RequestParam(value = "bio", required = false) String bio,
                                    jakarta.servlet.http.HttpServletResponse response,
                                    org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        var currentUserOpt = CurrentUser.get();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        var claims = currentUserOpt.get();
        String currentEmail = claims.email();

        try {
            mediator.send(new UpdateProfileInfoCommand(currentEmail, newEmail, fullName, username, bio));

            // E-posta adresi değiştiyse oturum cookie'sini yeni kimlik bilgileriyle güncelle
            String normalizedCurrent = (currentEmail != null) ? currentEmail.trim().toLowerCase(java.util.Locale.ROOT) : "";
            String normalizedNew = (newEmail != null) ? newEmail.trim().toLowerCase(java.util.Locale.ROOT) : "";
            if (!normalizedCurrent.equalsIgnoreCase(normalizedNew)) {
                if (tokenService != null && authCookies != null) {
                    String newToken = tokenService.generateAccessToken(normalizedNew, claims.role(), claims.tier());
                    authCookies.write(response, newToken, tokenService.getExpirationSeconds());
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Profil bilgileriniz başarıyla güncellendi.");
            return "redirect:/profile";
        } catch (IllegalArgumentException | tr.com.huseyinaydin.sharedkernel.exception.BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/edit-profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Profil güncellenirken bir hata oluştu: " + e.getMessage());
            return "redirect:/edit-profile";
        }
    }

    @GetMapping({"/artists", "/artists.xhtml"})
    public String artists(Model model) {
        if (CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        List<ResultArtistDto> artists = mediator.query(new GetFeaturedArtistsQuery());
        model.addAttribute("artists", artists != null ? artists : Collections.emptyList());
        return "artists";
    }

    @GetMapping({"/artists/{id}", "/artists/{id}.xhtml"})
    public String artistDetail(@PathVariable("id") String id, Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        ArtistDetailDto artist = mediator.query(new GetArtistDetailQuery(id));
        if (artist == null) {
            return "redirect:/artists";
        }

        model.addAttribute("artist", artist);
        model.addAttribute("songs", artist.getPopularSongs() != null ? artist.getPopularSongs() : Collections.emptyList());

        List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : Collections.emptyList());

        return "artist-detail";
    }

    @GetMapping({"/artist-details", "/artist-details.xhtml"})
    public String artistDetails(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "id", required = false) String id) {
        if (CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        if (id != null && !id.isBlank()) {
            return "redirect:/artists/" + id;
        }
        if (name != null && !name.isBlank()) {
            return "redirect:/artists/" + java.net.URLEncoder.encode(name.trim(), java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:/artists";
    }

    @GetMapping({"/genres", "/genres.xhtml"})
    public String genres(Model model) {
        if (CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        Map<String, Integer> counts = mediator.query(new GetGenreSongCountsQuery());
        model.addAttribute("genreCounts", counts != null ? counts : Collections.emptyMap());
        return "genres";
    }

    @GetMapping({"/playlists", "/playlists.xhtml"})
    public String playlists(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        List<PlaylistDetailDto> userPlaylists = mediator.query(new GetUserPlaylistsQuery(email));
        model.addAttribute("playlists", userPlaylists != null ? userPlaylists : Collections.emptyList());
        return "playlists";
    }

    @PostMapping({"/playlists", "/playlists.xhtml", "/playlists/create"})
    public String createPlaylist(@RequestParam("name") String name,
                                 @RequestParam(value = "description", required = false) String description,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lütfen bir playlist adı girin.");
            return "redirect:/playlists";
        }

        String trimmedName = name.trim();
        try {
            mediator.send(new CreatePlaylistCommand(trimmedName, description, email));
            redirectAttributes.addFlashAttribute("successMessage", "'" + trimmedName + "' playlisti başarıyla oluşturuldu.");
        } catch (IllegalArgumentException | tr.com.huseyinaydin.sharedkernel.exception.BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Playlist oluşturulurken hata meydana geldi: " + e.getMessage());
        }

        return "redirect:/playlists";
    }

    @GetMapping({"/playlists/{id}", "/playlists/{id}.xhtml"})
    public String playlistDetails(@PathVariable("id") String id, Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        PlaylistDetailDto playlist = mediator.query(new GetPlaylistByIdQuery(id));
        if (playlist == null) {
            return "redirect:/playlists";
        }

        model.addAttribute("playlist", playlist);
        model.addAttribute("songs", playlist.getSongs() != null ? playlist.getSongs() : Collections.emptyList());
        model.addAttribute("songCount", playlist.getSongCount());

        List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : Collections.emptyList());

        return "playlist-details";
    }

    @PostMapping({"/playlists/{id}/remove-song/{songId}", "/playlists/{id}/remove-song/{songId}.xhtml"})
    public String removeSongFromPlaylistRoute(@PathVariable("id") String id,
                                              @PathVariable("songId") String songId,
                                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        mediator.send(new RemoveSongFromPlaylistCommand(id, songId));
        redirectAttributes.addFlashAttribute("successMessage", "Şarkı çalma listesinden çıkarıldı.");
        return "redirect:/playlists/" + id;
    }

    @GetMapping({"/likes", "/likes.xhtml"})
    public String likes(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
        List<Map<String, Object>> likedSongs = (likedSongIds != null && !likedSongIds.isEmpty())
                ? mediator.query(new GetSongsByIdsQuery(likedSongIds))
                : Collections.emptyList();

        model.addAttribute("likedSongs", likedSongs);
        model.addAttribute("likedCount", likedSongs != null ? likedSongs.size() : 0);
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : Collections.emptyList());
        return "likes";
    }

    @GetMapping({"/membership", "/membership.xhtml"})
    public String membership(Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }
        String currentTier = mediator.query(new tr.com.huseyinaydin.application.membership.queries.GetUserMembershipTierQuery(email));
        model.addAttribute("currentTier", currentTier != null ? currentTier : "Basic");
        return "membership";
    }

    @GetMapping({"/search", "/search.xhtml"})
    public String search(@RequestParam(value = "q", required = false) String q, Model model) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        String query = (q != null) ? q.trim() : "";
        model.addAttribute("q", query);

        List<Map<String, Object>> searchResults = !query.isEmpty()
                ? mediator.query(new SearchSongsQuery(query))
                : Collections.emptyList();
        model.addAttribute("songs", searchResults != null ? searchResults : Collections.emptyList());

        List<Long> likedSongIds = mediator.query(new GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : Collections.emptyList());

        return "search";
    }
}
