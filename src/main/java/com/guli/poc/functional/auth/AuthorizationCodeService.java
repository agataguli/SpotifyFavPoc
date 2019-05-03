package com.guli.poc.functional.auth;

import org.springframework.stereotype.Service;

@Service
public interface AuthorizationCodeService {
    void addSpotifyAuthorizationCode(String scope, String code);
    String getSpotifyAuthorizationCode(String scope);
    boolean isScopeAccessed(String scope);
    String getScopeAuthorizationLink(String clientId, String scope);
}
