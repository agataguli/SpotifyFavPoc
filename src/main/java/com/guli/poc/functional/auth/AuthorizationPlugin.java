package com.guli.poc.functional.auth;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.util.SpotifyStatics;
import org.springframework.stereotype.Component;

@Component
public interface AuthorizationPlugin {
    AccessToken getAuthorizationToken(String clientId, String secret);
    String getAuthorizationAccountLink(String clientId, SpotifyStatics.Scope scope);
    AccessToken getScopeAuthorizationToken(String clientId, String secret, SpotifyStatics.Scope scope);
    boolean isScopeAccessed(String scope);
}
