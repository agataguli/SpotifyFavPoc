package com.guli.poc.functional.auth.spotify;

import com.guli.poc.domain.AccessToken;
import com.guli.poc.domain.migration.spotify.SpotifyAccessTokenDTO;
import com.guli.poc.functional.auth.AuthorizationCodeService;
import com.guli.poc.functional.auth.AuthorizationTokenService;
import com.guli.poc.util.JsonHelper;
import com.guli.poc.util.SpotifyStatics;
import com.guli.poc.util.UnirestUtil;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class SpotifyAuthorizationTokenService implements AuthorizationTokenService {

    private final AuthorizationCodeService codeService;

    @Value("${spotify-authcode-register-uri}")
    private String authCodeRegisterUri;

    @Autowired
    public SpotifyAuthorizationTokenService(SpotifyAuthorizationCodeService codeService) {
        this.codeService = codeService;
    }

    /**
     * Send a request for new access token to non-user data related spotify resources like playlists, available genres etc
     * @param clientId  registered app client id
     * @param secret registered app client secret key
     * @see <a href="https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow"></a>
     */
    @Override
    public AccessToken requestNonUserResourcesAccessToken(String clientId, String secret) {
        return getAccessToken(getBaseAuthorizationPostRequest(clientId, secret, SpotifyStatics.REQUEST_BODY_CLIENT_CREDENTIALS));
    }

    /**
     * Send a request for new access token to particular scope, which requires to enable access by saving
     * valid authorization code.
     * @see <a href="https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow"></a>
     * @param clientId registered app client id
     * @param secret registered app client secret key
     * @param scope scope this access token give an access
     * @return access token to given scope
     */
    @Override
    public AccessToken requestUserResourcesScopeAccessToken(String clientId, String secret, String scope) {
        if (!codeService.isScopeAccessed(scope)) {
            throw new IllegalArgumentException("Token cannot be reached as long as scope auth code is not saved");
        }
        String code = codeService.getSpotifyAuthorizationCode(scope);
        return getAccessToken(getScopeAuthorizationPostRequest(clientId, secret, code, SpotifyStatics.REQUEST_BODY_AUTHORIZATION_CODE));
    }

    private AccessToken getAccessToken(MultipartBody multipartBody) {
        AccessToken accessToken = null;
        try {
            accessToken = convertToAccessTokenObject(this.getSpotifyAccessTokenNode(multipartBody));
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    /**
     * Processes request for access token
     */
    private JsonNode getSpotifyAccessTokenNode(MultipartBody multipartBody) throws UnirestException {
        return multipartBody.asJson().getBody();
    }

    private MultipartBody getBaseAuthorizationPostRequest(String clientId, String secret, String grantType) {
        return UnirestUtil.getBaseAuthorizationPostRequest(grantType, getAuthorizationHeader(clientId, secret));
    }

    private MultipartBody getScopeAuthorizationPostRequest(String clientId, String secret, String code, String grantType) {
        return getBaseAuthorizationPostRequest(clientId, secret, grantType)
                .field(SpotifyStatics.REQUEST_BODY_CODE, code)
                .field(SpotifyStatics.REQUEST_BODY_REDIRECT_URI, authCodeRegisterUri);
    }

    private String getAuthorizationHeader(String clientId, String secret) {
        return SpotifyStatics.REQUEST_HEADER_BASIC + " " + getBase64EncodedClientIdSecret(clientId, secret);
    }

    private AccessToken convertToAccessTokenObject(JsonNode jsonNode) {
        SpotifyAccessTokenDTO spotifyAccessTokenDTO = JsonHelper.getFromJsonNode(jsonNode, SpotifyAccessTokenDTO.class);
        return spotifyAccessTokenDTO == null ? null : spotifyAccessTokenDTO.toAccessToken();
    }

    /**
     * Base 64 encoded 'clientId:secret' value
     */
    private String getBase64EncodedClientIdSecret(String clientId, String secret) {
        String wantedFormatValue = clientId + ":" + secret;
        return Base64.getEncoder().encodeToString(wantedFormatValue.getBytes());
    }
}
