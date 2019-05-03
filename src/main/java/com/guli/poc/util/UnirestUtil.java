package com.guli.poc.util;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;

public class UnirestUtil {
    private UnirestUtil() {

    }

    public static JsonNode requestFollowedArtistsPiece(String url, String authorizationHeaderValue) throws UnirestException {
        return Unirest.get(url)
                .header(SpotifyStatics.REQUEST_HEADER_AUTHORIZATION, authorizationHeaderValue)
                .asJson().getBody();
    }

    public static String getScopeAuthorizationLink(String clientId, String scope, String uri) {
        return Unirest.get(SpotifyStatics.ACCOUNT_AUTHORIZE_ENDPOINT)
                .queryString(SpotifyStatics.QUERY_CLIENT_ID, clientId)
                .queryString(SpotifyStatics.QUERY_RESPONSE_TYPE, SpotifyStatics.QUERY_CODE)
                .queryString(SpotifyStatics.QUERY_SCOPE, scope)
                .queryString(SpotifyStatics.QUERY_STATE, scope)
                .queryString(SpotifyStatics.QUERY_REDIRECT_URI, uri)
                .getUrl();
    }

    public static MultipartBody getBaseAuthorizationPostRequest(String grantType, String header) {
        return Unirest.post(SpotifyStatics.ACCOUNT_API_TOKEN_ENDPOINT)
                .header(SpotifyStatics.REQUEST_HEADER_AUTHORIZATION, header)
                .field(SpotifyStatics.REQUEST_BODY_GRANT_TYPE, grantType);
    }

    public static JsonNode requestAvailableGenres(String header) throws UnirestException {
        return Unirest.get(SpotifyStatics.API_ALL_GENRES_ENDPOINT)
                .header(SpotifyStatics.REQUEST_HEADER_ACCEPT, SpotifyStatics.REQUEST_HEADER_APPLICATION_JSON)
                .header(SpotifyStatics.REQUEST_HEADER_AUTHORIZATION, header)
                .asJson().getBody();
    }
}
