cat << 'INNER' > music-web-thymeleaf/src/main/java/tr/com/huseyinaydin/web/controllers/WebController.java
package tr.com.huseyinaydin.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.ports.IHomeService;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;

@Controller
public class WebController {

    @Autowired
    private IHomeService homeService;

    @GetMapping({"/", "/index.xhtml"})
    public String index(Model model) {
        HomeDashboardDto dashboardData = homeService.buildHomeDashboard("current-user");
        model.addAttribute("personalizedRecommendations", dashboardData.getPersonalizedRecommendations());
        model.addAttribute("topGlobalSongs", dashboardData.getTopGlobalSongs());
        return "index";
    }

    @GetMapping("/login.xhtml")
    public String login() {
        return "login";
    }

    @GetMapping("/register.xhtml")
    public String register() {
        return "register";
    }

    @GetMapping("/profile.xhtml")
    public String profile() {
        return "profile";
    }

    @GetMapping("/edit-profile.xhtml")
    public String editProfile() {
        return "edit-profile";
    }

    @GetMapping("/artists.xhtml")
    public String artists() {
        return "artists";
    }

    @GetMapping("/genres.xhtml")
    public String genres() {
        return "genres";
    }

    @GetMapping("/playlists.xhtml")
    public String playlists() {
        return "playlists";
    }

    @GetMapping("/likes.xhtml")
    public String likes() {
        return "likes";
    }

    @GetMapping("/membership.xhtml")
    public String membership() {
        return "membership";
    }

    @GetMapping("/search.xhtml")
    public String search(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("q", q);
        return "search";
    }
}
INNER
