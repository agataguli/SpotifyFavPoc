package com.guli.poc.functional.follow;

import com.guli.poc.domain.Artist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FollowedArtistPlugin {
    List<Artist> getFollowedArtists(String clientId, String secret) throws IllegalAccessException;
}
