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

@Controller
public class WebController {

    @Autowired
    private Mediator mediator;

    @Autowired(required = false)
    private tr.com.huseyinaydin.application.ports.IProfileService profileService;

    @Autowired(required = false)
    private tr.com.huseyinaydin.application.ports.ITokenService tokenService;

    @Autowired(required = false)
    private tr.com.huseyinaydin.web.security.AuthCookies authCookies;

    @GetMapping("favicon.ico")
    public org.springframework.http.ResponseEntity<Void> returnNoFavicon() {
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery(email));
        model.addAttribute("topGlobalSongs", dashboardData.getTopGlobalSongs());

        java.util.List<Long> likedSongIds = mediator.query(new tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : java.util.Collections.emptyList());

        return "index";
    }

    @GetMapping("/recommendations")
    public String recommendations(Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery(email));
        model.addAttribute("personalizedRecommendations", dashboardData.getPersonalizedRecommendations());

        java.util.List<Long> likedSongIds = mediator.query(new tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : java.util.Collections.emptyList());

        return "fragments/recommendations";
    }

    @GetMapping({"/login", "/login.xhtml"})
    public String login() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isPresent()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping({"/register", "/register.xhtml"})
    public String register() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isPresent()) {
            return "redirect:/";
        }
        return "register";
    }

    @GetMapping({"/profile", "/profile.xhtml"})
    public String profile(Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }
        tr.com.huseyinaydin.application.profile.dtos.UserProfileDto profile =
                (profileService != null) ? profileService.getProfile(email) : null;
        model.addAttribute("profile", profile);

        int playlistCount = 0;
        if (playlistService != null) {
            var pl = playlistService.getUserPlaylists(email);
            if (pl != null) playlistCount = pl.size();
        }
        model.addAttribute("playlistCount", playlistCount);

        int likeCount = 0;
        try {
            java.util.List<Long> likedSongIds = mediator.query(new tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery(email));
            if (likedSongIds != null) likeCount = likedSongIds.size();
        } catch (Exception ignored) {}
        model.addAttribute("likeCount", likeCount);

        return "profile";
    }

    @GetMapping({"/edit-profile", "/edit-profile.xhtml"})
    public String editProfile(Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }
        tr.com.huseyinaydin.application.profile.dtos.UserProfileDto profile =
                (profileService != null) ? profileService.getProfile(email) : null;
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
        var currentUserOpt = tr.com.huseyinaydin.web.security.CurrentUser.get();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        var claims = currentUserOpt.get();
        String currentEmail = claims.email();

        try {
            if (profileService != null) {
                profileService.updateProfileInfo(currentEmail, newEmail, fullName, username, bio);
            }

            // E-posta adresi değiştiyse çalma listeleri ve oturum güncellemesi yap
            String normalizedCurrent = (currentEmail != null) ? currentEmail.trim().toLowerCase(java.util.Locale.ROOT) : "";
            String normalizedNew = (newEmail != null) ? newEmail.trim().toLowerCase(java.util.Locale.ROOT) : "";
            if (!normalizedCurrent.equalsIgnoreCase(normalizedNew)) {
                if (playlistService != null) {
                    playlistService.migrateUser(normalizedCurrent, normalizedNew);
                }
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

    @Autowired(required = false)
    private tr.com.huseyinaydin.application.ports.IArtistService artistService;

    @GetMapping({"/artists", "/artists.xhtml"})
    public String artists(Model model) {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        java.util.List<tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto> artists =
                (artistService != null) ? artistService.getFeaturedArtists() : java.util.Collections.emptyList();
        model.addAttribute("artists", artists);
        return "artists";
    }

    @GetMapping({"/artist-details", "/artist-details.xhtml"})
    public String artistDetails(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "id", required = false) String id) {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        if (name != null && !name.isBlank()) {
            return "redirect:/search?q=" + java.net.URLEncoder.encode(name.trim(), java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:/artists";
    }

    @GetMapping({"/genres", "/genres.xhtml"})
    public String genres() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "genres";
    }

    @Autowired
    private tr.com.huseyinaydin.application.ports.IPlaylistService playlistService;

    @GetMapping({"/playlists", "/playlists.xhtml"})
    public String playlists(Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        java.util.List<tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto> userPlaylists =
                (playlistService != null) ? playlistService.getUserPlaylists(email) : java.util.Collections.emptyList();
        model.addAttribute("playlists", userPlaylists);
        return "playlists";
    }

    @PostMapping({"/playlists", "/playlists.xhtml", "/playlists/create"})
    public String createPlaylist(@RequestParam("name") String name,
                                 @RequestParam(value = "description", required = false) String description,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lütfen bir playlist adı girin.");
            return "redirect:/playlists";
        }

        String trimmedName = name.trim();
        if (playlistService != null) {
            java.util.List<tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto> userPlaylists =
                    playlistService.getUserPlaylists(email);
            boolean exists = userPlaylists.stream()
                    .anyMatch(p -> p.getName() != null && p.getName().trim().equalsIgnoreCase(trimmedName));
            if (exists) {
                redirectAttributes.addFlashAttribute("errorMessage", "'" + trimmedName + "' adında bir playlist zaten mevcut!");
                return "redirect:/playlists";
            }

            try {
                tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto dto =
                        new tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto();
                dto.setUserId(email);
                dto.setName(trimmedName);
                dto.setDescription(description != null ? description.trim() : "");
                playlistService.createPlaylist(dto);
                redirectAttributes.addFlashAttribute("successMessage", "'" + trimmedName + "' playlisti başarıyla oluşturuldu.");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/playlists";
            }
        }

        try {
            java.util.UUID userUuid = java.util.UUID.nameUUIDFromBytes(email.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            mediator.send(new tr.com.huseyinaydin.application.playlist.commands.CreatePlaylistCommand(trimmedName, userUuid));
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(WebController.class).warn("Create playlist command mediation warning: {}", e.getMessage());
        }

        return "redirect:/playlists";
    }

    @GetMapping({"/playlists/{id}", "/playlists/{id}.xhtml"})
    public String playlistDetails(@PathVariable("id") String id, Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto playlist =
                (playlistService != null) ? playlistService.getPlaylistById(id) : null;
        if (playlist == null) {
            return "redirect:/playlists";
        }

        model.addAttribute("playlist", playlist);
        model.addAttribute("songs", playlist.getSongs() != null ? playlist.getSongs() : java.util.Collections.emptyList());
        model.addAttribute("songCount", playlist.getSongCount());

        java.util.List<Long> likedSongIds = mediator.query(new tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : java.util.Collections.emptyList());

        return "playlist-details";
    }

    @PostMapping({"/playlists/{id}/remove-song/{songId}", "/playlists/{id}/remove-song/{songId}.xhtml"})
    public String removeSongFromPlaylistRoute(@PathVariable("id") String id,
                                             @PathVariable("songId") String songId,
                                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }
        if (playlistService != null) {
            playlistService.removeSongFromPlaylist(id, songId);
            redirectAttributes.addFlashAttribute("successMessage", "Şarkı çalma listesinden çıkarıldı.");
        }
        return "redirect:/playlists/" + id;
    }

    @GetMapping({"/likes", "/likes.xhtml"})
    public String likes(Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        java.util.List<Long> likedSongIds = mediator.query(new tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery(email));
        java.util.List<java.util.Map<String, Object>> likedSongs = (likedSongIds != null && !likedSongIds.isEmpty() && songSearchService != null)
                ? songSearchService.getSongsByIds(likedSongIds)
                : java.util.Collections.emptyList();

        model.addAttribute("likedSongs", likedSongs);
        model.addAttribute("likedCount", likedSongs.size());
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : java.util.Collections.emptyList());
        return "likes";
    }

    @GetMapping({"/membership", "/membership.xhtml"})
    public String membership() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "membership";
    }

    @Autowired
    private tr.com.huseyinaydin.application.ports.ISongSearchService songSearchService;

    @GetMapping({"/search", "/search.xhtml"})
    public String search(@RequestParam(value = "q", required = false) String q, Model model) {
        String email = tr.com.huseyinaydin.web.security.CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return "redirect:/login";
        }

        String query = (q != null) ? q.trim() : "";
        model.addAttribute("q", query);
        java.util.List<java.util.Map<String, Object>> searchResults = (!query.isEmpty() && songSearchService != null)
                ? songSearchService.searchSongs(query)
                : java.util.Collections.emptyList();
        model.addAttribute("songs", searchResults);

        java.util.List<Long> likedSongIds = mediator.query(new tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery(email));
        model.addAttribute("likedSongIds", likedSongIds != null ? likedSongIds : java.util.Collections.emptyList());

        return "search";
    }
}
