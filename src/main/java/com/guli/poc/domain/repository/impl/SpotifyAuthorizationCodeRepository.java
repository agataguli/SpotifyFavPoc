package com.guli.poc.domain.repository.impl;

import com.guli.poc.domain.repository.AuthorizationCodeRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SpotifyAuthorizationCodeRepository implements AuthorizationCodeRepository {
    private Map<String, String> authCodes = new HashMap<>();

    @Override
    public void addAuthorizationCode(String scope, String code) {
        authCodes.put(scope, code);
    }

    @Override
    public String getCode(String scope) {
        return authCodes.get(scope);
    }
}
