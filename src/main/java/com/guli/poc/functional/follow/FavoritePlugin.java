package com.guli.poc.functional.follow;

import com.guli.poc.domain.Artist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FavoritePlugin {
    Map<String, List<Artist>> getFavoriteGenresWithArtistsByFollowedArtists(String clientId, String secret, int limit) throws IllegalAccessException;
}
