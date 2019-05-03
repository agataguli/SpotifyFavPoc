package com.guli.poc.util;

public final class SpotifyStatics {
    private SpotifyStatics() throws IllegalAccessException {
        throw new IllegalAccessException("This class is not intended to be initiated");
    }

    // url
    private final static String SPOTIFY_ACCOUNTS_URL = "https://accounts.spotify.com";
    public final static String ACCOUNT_API_TOKEN_ENDPOINT = SPOTIFY_ACCOUNTS_URL + "/api/token";
    public final static String ACCOUNT_AUTHORIZE_ENDPOINT = SPOTIFY_ACCOUNTS_URL + "/authorize";

    private final static String SPOTIFY_API_URL = "https://api.spotify.com/v1";
    public final static String API_ALL_GENRES_ENDPOINT = SPOTIFY_API_URL + "/recommendations/available-genre-seeds";
    public final static String API_FOLLOWING_ENDPOINT = SPOTIFY_API_URL + "/me/following";

    // QUERY PART
    public final static String QUERY_ARTIST = "artist";
    public final static String QUERY_CLIENT_ID = "client_id";
    public final static String QUERY_CODE = "code";
    public final static String QUERY_REDIRECT_URI = "redirect_uri";
    public final static String QUERY_RESPONSE_TYPE = "response_type";
    public final static String QUERY_SCOPE = "scope";
    public final static String QUERY_STATE = "state";
    public final static String QUERY_TYPE = "type";

    // request body part
    public final static String REQUEST_BODY_AUTHORIZATION_CODE = "authorization_code";
    public final static String REQUEST_BODY_CODE = "code";
    public final static String REQUEST_BODY_CLIENT_CREDENTIALS = "client_credentials";
    public final static String REQUEST_BODY_GRANT_TYPE = "grant_type";
    public final static String REQUEST_BODY_REDIRECT_URI = "redirect_uri";
    public final static String REQUEST_BODY_REFRESH_TOKEN = "refresh_token";

    // request headers
    public final static String REQUEST_HEADER_ACCEPT = "Accept";
    public final static String REQUEST_HEADER_APPLICATION_JSON = "application/json";
    public final static String REQUEST_HEADER_AUTHORIZATION = "Authorization";
    public final static String REQUEST_HEADER_BASIC = "Basic";

    public enum Scope {
        USER_FOLLOW_READ("user-follow-read");

        String scope;

        Scope(String scope) {
            this.scope = scope;
        }

        public String getScope() {
            return this.scope;
        }
    }
}
