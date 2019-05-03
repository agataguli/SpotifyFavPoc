package com.guli.poc.functional.follow.impl.spotify;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.domain.Artist;
import com.guli.poc.domain.migration.spotify.FollowedArtistsResponseDTO;
import com.guli.poc.functional.auth.AuthorizationPlugin;
import com.guli.poc.functional.follow.FollowedArtistPlugin;
import com.guli.poc.util.JsonHelper;
import com.guli.poc.util.SpotifyStatics;
import com.guli.poc.util.UnirestUtil;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpotifyFollowedArtistsPlugin implements FollowedArtistPlugin {

    private final AuthorizationPlugin authorizationPlugin;

    @Autowired
    public SpotifyFollowedArtistsPlugin(AuthorizationPlugin authorizationPlugin) {
        this.authorizationPlugin = authorizationPlugin;
    }

    @Override
    public List<Artist> getFollowedArtists(String clientId, String secret) throws IllegalAccessException {
        if (!authorizationPlugin.isScopeAccessed(SpotifyStatics.Scope.USER_FOLLOW_READ.getScope())) {
            throw new IllegalAccessException("Scope is not accessed");
        }
        AccessToken accessToken = authorizationPlugin.getScopeAuthorizationToken(clientId, secret, SpotifyStatics.Scope.USER_FOLLOW_READ);
        return requestFollowedArtists(accessToken.getSpotifyAuthorizationHeader());
    }

    private List<Artist> requestFollowedArtists(String authorizationHeaderValue) {
        String url = getPrimaryUrl();
        List<Artist> followedArtists = new ArrayList<>();
        FollowedArtistsResponseDTO dto;

        while (url != null) {
            dto = requestFollowedArtistsPiece(url, authorizationHeaderValue);
            if (dto == null) {
                break;
            }
            followedArtists.addAll(dto.toArtistList());
            url = dto.getNext();
        }

        return followedArtists;
    }

    private FollowedArtistsResponseDTO requestFollowedArtistsPiece(String url, String authorizationHeaderValue) {
        FollowedArtistsResponseDTO dto = null;
        try {
            JsonNode jsonNode = UnirestUtil.requestFollowedArtistsPiece(url, authorizationHeaderValue);
            dto = toFollowedArtistsResponseDTO(jsonNode);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return dto;
    }

    private String getPrimaryUrl() {
        return Unirest.get(SpotifyStatics.API_FOLLOWING_ENDPOINT)
                .queryString(SpotifyStatics.QUERY_TYPE, SpotifyStatics.QUERY_ARTIST)
                .getUrl();
    }

    private FollowedArtistsResponseDTO toFollowedArtistsResponseDTO(JsonNode jsonNode) {
        return JsonHelper.getFromJsonNode(jsonNode, FollowedArtistsResponseDTO.class);
    }
}
