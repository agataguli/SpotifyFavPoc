package com.guli.poc.functional.auth;

import com.guli.poc.domain.AccessToken;
import org.springframework.stereotype.Service;

@Service
public interface AuthorizationTokenService {
    AccessToken requestNonUserResourcesAccessToken(String clientId, String secret);
    AccessToken requestUserResourcesScopeAccessToken(String clientId, String secret, String scope);
}
