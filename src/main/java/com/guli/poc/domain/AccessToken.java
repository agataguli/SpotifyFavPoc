package com.guli.poc.domain;

public class AccessToken {
    private String value;
    private String type;

    public AccessToken(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getSpotifyAuthorizationHeader() {
        return type + " " + value;
    }
}
