package com.guli.poc;

import com.guli.poc.domain.Artist;
import com.guli.poc.functional.auth.AuthorizationPlugin;
import com.guli.poc.functional.browse.GenrePlugin;
import com.guli.poc.functional.follow.FavoritePlugin;
import com.guli.poc.functional.follow.FollowedArtistPlugin;
import com.guli.poc.util.SpotifyStatics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class DummyFavoriteGenresExample implements ApplicationRunner {

    @Value("${spotify-client-id}")
    private String clientId;

    @Value("${spotify-secret}")
    private String secret;

    private final AuthorizationPlugin authorizationPlugin;
    private final FavoritePlugin favoritePlugin;

    @Autowired
    public DummyFavoriteGenresExample(AuthorizationPlugin authorizationPlugin,
                                      FollowedArtistPlugin followedArtistPlugin,
                                      GenrePlugin genrePlugin, FavoritePlugin favoritePlugin) {
        this.authorizationPlugin = authorizationPlugin;
        this.favoritePlugin = favoritePlugin;
    }

    public void run(ApplicationArguments args) {
        // this is just a poc of retrieving followed artists and related genres
        SpotifyStatics.Scope scope = SpotifyStatics.Scope.USER_FOLLOW_READ;
        sendScopeAuthorizationRequest(scope);
        do {
            System.out.println("If you ready please put Y to continue: ");
        } while (!isPOCActionAvailable(scope.getScope()));
        displayTop3Genres(); //dummy but refresh tokens are not impplemented :P
        System.exit(0);
    }

    private void sendScopeAuthorizationRequest(SpotifyStatics.Scope scope) {
        String tempAuthorizationURL = this.authorizationPlugin.getAuthorizationAccountLink(clientId, scope);
        System.out.println("Please allow access to scope " + scope.getScope() + ": " + tempAuthorizationURL);
    }

    private void displayTop3Genres() {
        try {
            Map<String, List<Artist>> map = favoritePlugin.getFavoriteGenresWithArtistsByFollowedArtists(clientId, secret, 3);
            System.out.println("YOUR BESTS:");
            for (Map.Entry<String, List<Artist>> m : map.entrySet()) {
                System.out.println("#" + m.getKey() + ", artists: " + m.getValue().size());
                m.getValue().forEach(a -> System.out.println("- " + a.getName()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private boolean isPOCActionAvailable(String scope) {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().equals("Y") && this.authorizationPlugin.isScopeAccessed(scope);
    }
}
