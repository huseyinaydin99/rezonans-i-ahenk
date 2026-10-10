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
        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery("current-user"));
        model.addAttribute("topGlobalSongs", dashboardData.getTopGlobalSongs());
        return "index";
    }

    @GetMapping("/recommendations")
    public String recommendations(Model model) {
        HomeDashboardDto dashboardData = mediator.query(new GetHomeDashboardQuery("current-user"));
        model.addAttribute("personalizedRecommendations", dashboardData.getPersonalizedRecommendations());
        return "fragments/recommendations";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile() {
        return "edit-profile";
    }

    @GetMapping("/artists")
    public String artists() {
        return "artists";
    }

    @GetMapping("/genres")
    public String genres() {
        return "genres";
    }

    @GetMapping("/playlists")
    public String playlists() {
        return "playlists";
    }

    @GetMapping("/likes")
    public String likes() {
        return "likes";
    }

    @GetMapping("/membership")
    public String membership() {
        return "membership";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("q", q);
        return "search";
    }
}
