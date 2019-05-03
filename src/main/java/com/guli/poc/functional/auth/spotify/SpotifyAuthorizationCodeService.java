package com.guli.poc.functional.auth.spotify;

import com.guli.poc.domain.repository.AuthorizationCodeRepository;
import com.guli.poc.domain.repository.impl.SpotifyAuthorizationCodeRepository;
import com.guli.poc.functional.auth.AuthorizationCodeService;
import com.guli.poc.util.SpotifyStatics;
import com.guli.poc.util.UnirestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SpotifyAuthorizationCodeService implements AuthorizationCodeService {

    @Value("${spotify-authcode-register-uri}")
    private String authCodeRegisterUri;

    private final AuthorizationCodeRepository codeRepository;

    @Autowired
    public SpotifyAuthorizationCodeService(SpotifyAuthorizationCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    /**
     * Save authorization code and related scope pair if it is valid.
     * @throws IllegalArgumentException if scope if not valid
     * @param scope scope this code enables access
     * @param code code giving an access to given scope
     */
    @Override
    public void addSpotifyAuthorizationCode(String scope, String code) {
        if (!isScopeValid(scope)) {
            throw new IllegalArgumentException("Cannot save auth code with illegal scope: " + scope);
        }
        codeRepository.addAuthorizationCode(scope, code);
    }

    /**
     * Code which enables access to specific scope endpoint.
     * @param scope required to perform particular action
     * @return code
     */
    @Override
    public String getSpotifyAuthorizationCode(String scope) {
        return codeRepository.getCode(scope);
    }

    /**
     * Check requested scope is already enabled for app to access
     * @param scope to check
     * @return boolean flag
     */
    @Override
    public boolean isScopeAccessed(String scope) {
        return isScopeValid(scope) && getSpotifyAuthorizationCode(scope) != null;
    }

    /**
     * Return url which can be used to authorize access within the scopes
     * @param clientId id of spotify registered application
     * @param scope which this url can be accessed
     * @return url
     */
    @Override
    public String getScopeAuthorizationLink(String clientId, String scope) {
        return UnirestUtil.getScopeAuthorizationLink(clientId, scope, authCodeRegisterUri);
    }

    /**
     * Check this scope is on app available scopes.
     * @param scope to check
     * @return boolean flag
     */
    private boolean isScopeValid(String scope) {
        return Arrays.stream(SpotifyStatics.Scope.values()).anyMatch(s -> s.getScope().equals(scope));
    }
}
