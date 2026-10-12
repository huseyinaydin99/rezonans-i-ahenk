package tr.com.huseyinaydin.web.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TemplateRenderingTest {

    private SpringTemplateEngine engine;
    private JakartaServletWebApplication webApp;
    private MockServletContext servletContext;

    @BeforeEach
    void setUp() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        GenericApplicationContext appCtx = new GenericApplicationContext();
        appCtx.refresh();
        resolver.setApplicationContext(appCtx);
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect());

        servletContext = new MockServletContext();
        webApp = JakartaServletWebApplication.buildApplication(servletContext);
    }

    private WebContext createContext() {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        MockHttpServletResponse response = new MockHttpServletResponse();
        var exchange = webApp.buildExchange(request, response);
        return new WebContext(exchange);
    }

    @Test
    void testIndexTemplateRendersWithoutSpelError() {
        WebContext context = createContext();
        HomeDashboardDto.SongSnippet snippetWithUrl = new HomeDashboardDto.SongSnippet(
                1L, "Enjoy The Silence", "Depeche Mode", "https://example.com/cover.jpg");
        HomeDashboardDto.SongSnippet snippetWithoutUrl = new HomeDashboardDto.SongSnippet(
                2L, "Personal Jesus", "Depeche Mode", null);

        context.setVariable("topGlobalSongs", List.of(snippetWithUrl, snippetWithoutUrl));
        context.setVariable("likedSongIds", Collections.emptyList());

        assertDoesNotThrow(() -> {
            String result = engine.process("index", context);
            assertNotNull(result);
        });
    }

    @Test
    void testRecommendationsFragmentRendersWithoutSpelError() {
        WebContext context = createContext();
        HomeDashboardDto.SongSnippet snippet = new HomeDashboardDto.SongSnippet(
                1L, "River of Darkness", "The Midnight", null);

        context.setVariable("personalizedRecommendations", List.of(snippet));
        context.setVariable("likedSongIds", Collections.emptyList());

        assertDoesNotThrow(() -> {
            String result = engine.process("fragments/recommendations", context);
            assertNotNull(result);
        });
    }

    @Test
    void testLikesTemplateRendersWithoutSpelError() {
        WebContext context = createContext();
        Map<String, Object> song = Map.of(
                "id", 1L,
                "title", "Enjoy The Silence",
                "artist", "Depeche Mode",
                "cover", "https://example.com/cover.jpg"
        );
        Map<String, Object> songNoCover = Map.of(
                "id", 2L,
                "title", "Personal Jesus",
                "artist", "Depeche Mode"
        );

        context.setVariable("likedSongs", List.of(song, songNoCover));
        context.setVariable("likedCount", 2);
        context.setVariable("likedSongIds", List.of(1L, 2L));

        assertDoesNotThrow(() -> {
            String result = engine.process("likes", context);
            assertNotNull(result);
        });
    }

    @Test
    void testSearchTemplateRendersWithoutSpelError() {
        WebContext context = createContext();
        Map<String, Object> song = Map.of(
                "id", 1L,
                "title", "Enjoy The Silence",
                "artist", "Depeche Mode",
                "tier", "Premium",
                "cover", "https://example.com/cover.jpg"
        );

        context.setVariable("songs", List.of(song));
        context.setVariable("q", "Depeche");
        context.setVariable("likedSongIds", List.of(1L));

        assertDoesNotThrow(() -> {
            String result = engine.process("search", context);
            assertNotNull(result);
        });
    }

    @Test
    void testArtistDetailTemplateRendersSuccessfully() {
        WebContext context = createContext();
        tr.com.huseyinaydin.application.artist.dtos.ArtistSongDto song1 = new tr.com.huseyinaydin.application.artist.dtos.ArtistSongDto(
                30L, "Sunset", "https://example.com/sunset.jpg", "https://example.com/sunset.mp3",
                "5:26", 2310000L, "Gold", 2, "The Midnight", "Synthwave"
        );
        tr.com.huseyinaydin.application.artist.dtos.ArtistSongDto song2 = new tr.com.huseyinaydin.application.artist.dtos.ArtistSongDto(
                31L, "Days of Thunder", null, "https://example.com/thunder.mp3",
                "5:24", 1980000L, "Basic", 1, "The Midnight", "Synthwave"
        );
        tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto artist = new tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto(
                "art-2", "The Midnight", "/images/artists/midnight-cockpit.svg",
                "Synthwave efsanesi", 1002914L, true, List.of(song1, song2)
        );

        context.setVariable("artist", artist);
        context.setVariable("songs", artist.getPopularSongs());
        context.setVariable("likedSongIds", List.of(30L));

        assertDoesNotThrow(() -> {
            String result = engine.process("artist-detail", context);
            assertNotNull(result);
        });
    }

    @Test
    void testArtistsTemplateRendersSuccessfully() {
        WebContext context = createContext();
        tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto artistDto = new tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto(
                "art-1", "The Midnight & Nikki Flores", "/images/artists/midnight-cockpit.svg", 1893317L, true
        );
        context.setVariable("artists", List.of(artistDto));

        assertDoesNotThrow(() -> {
            String result = engine.process("artists", context);
            assertNotNull(result);
        });
    }

    @Test
    void testGenreDetailTemplateRendersSuccessfully() {
        WebContext context = createContext();
        tr.com.huseyinaydin.application.genre.dtos.GenreSongDto song1 = new tr.com.huseyinaydin.application.genre.dtos.GenreSongDto(
                30L, "Sunset", "The Midnight", "https://example.com/sunset.jpg", "https://example.com/sunset.mp3",
                "5:26", 2310000L, "Gold", 2, "Synthwave"
        );
        tr.com.huseyinaydin.application.genre.dtos.GenreWithSongsDto genre = new tr.com.huseyinaydin.application.genre.dtos.GenreWithSongsDto(
                "synthwave", "Synthwave", "#SYNTHWAVE", "Neon ses evreni", "gn-card--synthwave", "#ff2a85", 1, List.of(song1)
        );

        context.setVariable("genre", genre);
        context.setVariable("songs", genre.getSongs());
        context.setVariable("likedSongIds", List.of(30L));

        assertDoesNotThrow(() -> {
            String result = engine.process("genre-detail", context);
            assertNotNull(result);
        });
    }

    @Test
    void testGenresTemplateRendersSuccessfully() {
        WebContext context = createContext();
        context.setVariable("genreCounts", Map.of("Pop", 11, "Rock", 6, "Synthwave", 21));

        assertDoesNotThrow(() -> {
            String result = engine.process("genres", context);
            assertNotNull(result);
        });
    }

    @Test
    void test403TemplateRendersSuccessfully() {
        WebContext context = createContext();
        context.setVariable("currentTier", "Basic");
        context.setVariable("requiredTier", "Premium");

        assertDoesNotThrow(() -> {
            String result = engine.process("403", context);
            assertNotNull(result);
        });
    }

    @Test
    void testError403TemplateRendersSuccessfully() {
        WebContext context = createContext();
        context.setVariable("currentTier", "Gold");
        context.setVariable("requiredTier", "Elit");

        assertDoesNotThrow(() -> {
            String result = engine.process("error/403", context);
            assertNotNull(result);
        });
    }
}
