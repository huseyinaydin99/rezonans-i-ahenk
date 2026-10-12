package tr.com.huseyinaydin.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;
import tr.com.huseyinaydin.application.home.queries.GetHomeDashboardQuery;

@Controller
public class WebController {

    @Autowired
    private Mediator mediator;

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
    public String profile() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "profile";
    }

    @GetMapping({"/edit-profile", "/edit-profile.xhtml"})
    public String editProfile() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "edit-profile";
    }

    @GetMapping({"/artists", "/artists.xhtml"})
    public String artists() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "artists";
    }

    @GetMapping({"/genres", "/genres.xhtml"})
    public String genres() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "genres";
    }

    @GetMapping({"/playlists", "/playlists.xhtml"})
    public String playlists() {
        if (tr.com.huseyinaydin.web.security.CurrentUser.get().isEmpty()) {
            return "redirect:/login";
        }
        return "playlists";
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
