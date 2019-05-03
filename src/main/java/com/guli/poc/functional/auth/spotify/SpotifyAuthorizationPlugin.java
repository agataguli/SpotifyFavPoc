package com.guli.poc.functional.auth.spotify;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.functional.auth.AuthorizationCodeService;
import com.guli.poc.functional.auth.AuthorizationPlugin;
import com.guli.poc.functional.auth.AuthorizationTokenService;
import com.guli.poc.util.SpotifyStatics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpotifyAuthorizationPlugin implements AuthorizationPlugin {

    private final AuthorizationCodeService codeService;
    private final AuthorizationTokenService tokenService;

    @Autowired
    public SpotifyAuthorizationPlugin(SpotifyAuthorizationCodeService codeService, SpotifyAuthorizationTokenService tokenService) {
        this.codeService = codeService;
        this.tokenService = tokenService;
    }

    /**
     * Returns access token to impl platform data
     *
     * @param clientId registered app client id
     * @param secret   registered app client secret key
     * @return received accessToken
     * @see <a href="https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow"></a>
     */
    public AccessToken getAuthorizationToken(String clientId, String secret) {
        return tokenService.requestNonUserResourcesAccessToken(clientId, secret);
    }

    /**
     * Returns impl url to give an access to valid scope
     *
     * @param clientId registered app client id
     * @param scope    requested scope
     * @return received login link
     * @see <a href="https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow"></a>
     */
    public String getAuthorizationAccountLink(String clientId, SpotifyStatics.Scope scope) {
        return codeService.getScopeAuthorizationLink(clientId, scope.getScope());
    }

    /**
     * Return access token for requested scope
     * @param clientId registered app client id
     * @param secret registered app secret key value, secures your client app
     * @param scope requested scope
     * @return token
     */
    public AccessToken getScopeAuthorizationToken(String clientId, String secret, SpotifyStatics.Scope scope) {
        return tokenService.requestUserResourcesScopeAccessToken(clientId, secret, scope.getScope());
    }

    /*
     * Check requested scope is already enabled for app to access
     * @param scope to check
     * @return boolean flag
     */
    @Override
    public boolean isScopeAccessed(String scope) {
        return codeService.isScopeAccessed(scope);
    }
}
