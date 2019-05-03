package com.guli.poc.domain.migration.spotify;

import com.guli.poc.domain.AccessToken;

public class SpotifyAccessTokenDTO {
    public String access_token;
    public String token_type;

    public AccessToken toAccessToken() {
        return new AccessToken(access_token, token_type);
    }
}
